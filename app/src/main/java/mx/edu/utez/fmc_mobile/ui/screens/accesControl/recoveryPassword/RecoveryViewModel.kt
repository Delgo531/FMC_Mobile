package mx.edu.utez.fmc_mobile.ui.screens.accesControl.recoveryPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.dto.request.ForgotPasswordRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.ResetPasswordRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.VerifyResetCodeRequest
import mx.edu.utez.fmc_mobile.data.repository.AuthRepository
import mx.edu.utez.fmc_mobile.utils.PasswordValidator
import mx.edu.utez.fmc_mobile.utils.SessionManager

class RecoveryViewModel : ViewModel() {

    companion object {
        private const val STAGE_NO_ACTIVE_RESET = "NO_ACTIVE_RESET"
        private const val STAGE_CODE_SENT = "CODE_SENT"
        private const val STAGE_CODE_VERIFIED = "CODE_VERIFIED"
        private const val STAGE_CODE_EXPIRED = "CODE_EXPIRED"
        private const val STAGE_COOLDOWN = "COOLDOWN"
    }

    private val repository = AuthRepository()

    var savedEmail: String = SessionManager.getRecoveryEmail()
    var savedResetToken: String = SessionManager.getRecoveryResetToken()

    private val _recoveryState = MutableStateFlow<RecoveryState>(RecoveryState.Idle)
    val recoveryState: StateFlow<RecoveryState> = _recoveryState

    fun forgotPassword(email: String) {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _recoveryState.value = RecoveryState.Error("Ingresa un correo válido")
            return
        }

        viewModelScope.launch {
            _recoveryState.value = RecoveryState.Loading
            try {
                val response = repository.forgotPassword(ForgotPasswordRequest(email))
                if (response.isSuccessful) {
                    savedEmail = email
                    savedResetToken = ""
                    SessionManager.savePasswordRecoverySession(email, STAGE_CODE_SENT)
                    _recoveryState.value = RecoveryState.EmailSent
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (e: Exception) {
                        "Error al enviar el correo"
                    }
                    _recoveryState.value = RecoveryState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _recoveryState.value = RecoveryState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resumeRecoveryFlow() {
        if (savedEmail.isBlank()) return

        viewModelScope.launch {
            _recoveryState.value = RecoveryState.Loading
            try {
                val response = repository.getPasswordResetStatus(savedEmail)
                if (!response.isSuccessful) {
                    _recoveryState.value = RecoveryState.Error("No se pudo validar la recuperación")
                    return@launch
                }

                val status = response.body()?.data
                if (status == null) {
                    _recoveryState.value = RecoveryState.Idle
                    return@launch
                }

                SessionManager.updatePasswordRecoveryStage(status.stage)

                when (status.stage) {
                    STAGE_CODE_SENT -> _recoveryState.value = RecoveryState.EmailSent

                    STAGE_CODE_VERIFIED -> {
                        _recoveryState.value = if (savedResetToken.isNotBlank()) {
                            RecoveryState.CodeVerified
                        } else {
                            RecoveryState.EmailSent
                        }
                    }

                    STAGE_CODE_EXPIRED -> {
                        savedResetToken = ""
                        SessionManager.savePasswordRecoverySession(savedEmail, STAGE_CODE_EXPIRED)
                        _recoveryState.value = RecoveryState.Error("Tu código expiró. Vuelve a solicitar recuperación.")
                    }

                    STAGE_COOLDOWN -> {
                        _recoveryState.value = RecoveryState.Error(
                            "Ya tienes una recuperación activa. Continúa con el código enviado."
                        )
                    }

                    STAGE_NO_ACTIVE_RESET -> {
                        clearRecoverySession()
                        _recoveryState.value = RecoveryState.Idle
                    }

                    else -> _recoveryState.value = RecoveryState.Idle
                }
            } catch (e: Exception) {
                _recoveryState.value = RecoveryState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun verifyCode(code: String) {
        val cleanCode = code.filter { it.isDigit() }

        if (cleanCode.isBlank() || cleanCode.length != 4) {
            _recoveryState.value = RecoveryState.Error("Ingresa el código de 4 dígitos")
            return
        }

        viewModelScope.launch {
            _recoveryState.value = RecoveryState.Loading
            try {
                val response = repository.verifyResetCode(
                    VerifyResetCodeRequest(email = savedEmail, code = cleanCode)
                )
                if (response.isSuccessful) {
                    // API returns StandardResponse: { status, message, data: { resetToken } }
                    val body = response.body()
                    val data = body?.get("data") as? Map<*, *>
                    savedResetToken = data?.get("resetToken")?.toString() ?: ""
                    SessionManager.savePasswordRecoverySession(
                        email = savedEmail,
                        stage = STAGE_CODE_VERIFIED,
                        resetToken = savedResetToken
                    )
                    _recoveryState.value = RecoveryState.CodeVerified
                } else {
                    val errorBody = response.errorBody()?.string()
                    try {
                        val errorJson = org.json.JSONObject(errorBody ?: "")
                        val errorCode = errorJson.optString("errorCode")
                        val errorMessage = errorJson.optString("message", "Código incorrecto")

                        if (errorCode == "IDENTITY_VERIFICATION_FAILED") {
                            _recoveryState.value = RecoveryState.IdentityVerificationFailed(errorMessage)
                        } else {
                            _recoveryState.value = RecoveryState.Error(errorMessage)
                        }
                    } catch (e: Exception) {
                        _recoveryState.value = RecoveryState.Error("Código incorrecto")
                    }
                }
            } catch (e: Exception) {
                _recoveryState.value = RecoveryState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetPassword(newPassword: String, confirmPassword: String) {
        if (newPassword.isBlank()) {
            _recoveryState.value = RecoveryState.Error("Ingresa la nueva contrasena")
            return
        }

        if (!PasswordValidator.evaluate(newPassword).isValid) {
            _recoveryState.value = RecoveryState.Error("La contrasena debe tener minimo 8 caracteres, mayuscula, minuscula y un caracter especial.")
            return
        }

        if (newPassword != confirmPassword) {
            _recoveryState.value = RecoveryState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _recoveryState.value = RecoveryState.Loading
            try {
                val response = repository.resetPassword(
                    ResetPasswordRequest(
                        resetToken = savedResetToken,
                        newPassword = newPassword,
                        confirmPassword = confirmPassword
                    )
                )
                if (response.isSuccessful) {
                    clearRecoverySession()
                    _recoveryState.value = RecoveryState.PasswordReset
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (e: Exception) {
                        "Error al actualizar la contraseña"
                    }
                    _recoveryState.value = RecoveryState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _recoveryState.value = RecoveryState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _recoveryState.value = RecoveryState.Idle
    }

    fun clearRecoverySession() {
        savedEmail = ""
        savedResetToken = ""
        SessionManager.clearPasswordRecoverySession()
    }
}

sealed class RecoveryState {
    object Idle : RecoveryState()
    object Loading : RecoveryState()
    object EmailSent : RecoveryState()
    object CodeVerified : RecoveryState()
    object PasswordReset : RecoveryState()
    data class IdentityVerificationFailed(val message: String) : RecoveryState()
    data class Error(val message: String) : RecoveryState()
}