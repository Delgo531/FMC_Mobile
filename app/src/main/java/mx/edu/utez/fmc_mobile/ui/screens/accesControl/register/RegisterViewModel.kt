package mx.edu.utez.fmc_mobile.ui.screens.accesControl.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.dto.request.RegisterRequest
import mx.edu.utez.fmc_mobile.data.repository.AuthRepository

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    private val _usernameError = MutableStateFlow(false)
    val usernameError: StateFlow<Boolean> = _usernameError

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError

    private val _passwordError = MutableStateFlow(false)
    val passwordError: StateFlow<Boolean> = _passwordError

    private val _municipioError = MutableStateFlow(false)
    val municipioError: StateFlow<Boolean> = _municipioError

    private fun validate(
        username: String,
        email: String,
        password: String,
        municipality: String
    ): Boolean {
        _usernameError.value = username.isBlank()
        _emailError.value = email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        _passwordError.value = password.isBlank() || password.length < 8
        _municipioError.value = municipality.isBlank()

        if (_usernameError.value) {
            _registerState.value = RegisterState.Error("El nombre de usuario es obligatorio")
            return false
        }
        if (_emailError.value) {
            _registerState.value = RegisterState.Error("Ingresa un correo válido")
            return false
        }
        if (_passwordError.value) {
            _registerState.value = RegisterState.Error("La contraseña debe tener mínimo 8 caracteres")
            return false
        }
        if (_municipioError.value) {
            _registerState.value = RegisterState.Error("Selecciona un municipio")
            return false
        }
        return true
    }

    fun register(
        username: String,
        email: String,
        password: String,
        municipality: String
    ) {
        if (!validate(username, email, password, municipality)) return

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val response = repository.register(
                    RegisterRequest(
                        username = username,
                        email = email,
                        password = password,
                        municipality = municipality
                    )
                )
                if (response.isSuccessful) {
                    _registerState.value = RegisterState.Success
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val json = org.json.JSONObject(errorBody ?: "")
                        json.getString("message")
                    } catch (e: Exception) {
                        "Error al registrarse"
                    }
                    _registerState.value = RegisterState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}