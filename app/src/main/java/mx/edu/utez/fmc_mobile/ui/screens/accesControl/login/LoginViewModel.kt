package mx.edu.utez.fmc_mobile.ui.screens.accesControl.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.dto.request.LoginRequest
import mx.edu.utez.fmc_mobile.data.repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _usernameError = MutableStateFlow(false)
    val usernameError: StateFlow<Boolean> = _usernameError

    private val _passwordError = MutableStateFlow(false)
    val passwordError: StateFlow<Boolean> = _passwordError

    private fun validate(username: String, password: String): Boolean {
        _usernameError.value = username.isBlank()
        _passwordError.value = password.isBlank()

        if (_usernameError.value) {
            _loginState.value = LoginState.Error("El usuario es obligatorio")
            return false
        }
        if (_passwordError.value) {
            _loginState.value = LoginState.Error("La contraseña es obligatoria")
            return false
        }
        return true
    }

    fun login(username: String, password: String) {
        if (!validate(username, password)) return

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = repository.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    _loginState.value = LoginState.Success
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (e: Exception) {
                        "Usuario o contraseña incorrectos"
                    }
                    _loginState.value = LoginState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}