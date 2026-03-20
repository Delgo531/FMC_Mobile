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

class RecoveryViewModel : ViewModel() {

    private val repository = AuthRepository()

    var savedEmail: String = ""
    var savedResetToken: String = ""

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
                    _recoveryState.value = RecoveryState.CodeVerified
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (e: Exception) {
                        "Código incorrecto"
                    }
                    _recoveryState.value = RecoveryState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _recoveryState.value = RecoveryState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetPassword(newPassword: String, confirmPassword: String) {
        if (newPassword.isBlank() || newPassword.length < 8) {
            _recoveryState.value = RecoveryState.Error("La contraseña debe tener mínimo 8 caracteres")
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
}

sealed class RecoveryState {
    object Idle : RecoveryState()
    object Loading : RecoveryState()
    object EmailSent : RecoveryState()
    object CodeVerified : RecoveryState()
    object PasswordReset : RecoveryState()
    data class Error(val message: String) : RecoveryState()
}