package mx.edu.utez.fmc_mobile.ui.screens.accesControl.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.RetrofitClient
import mx.edu.utez.fmc_mobile.data.remote.dto.request.LoginRequest
import mx.edu.utez.fmc_mobile.data.repository.AuthRepository
import mx.edu.utez.fmc_mobile.utils.SessionManager

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
                    val body = response.body()
                    if (body != null) {
                        // API returns StandardResponse: { status, message, data: { token } }
                        val data = body["data"] as? Map<*, *>
                        val token = data?.get("token")?.toString()
                        if (token != null) {
                            SessionManager.saveToken(token)
                            // Fetch user profile to save user data
                            fetchAndSaveUserProfile(username)
                            _loginState.value = LoginState.Success
                        } else {
                            _loginState.value = LoginState.Error("No se recibió token de autenticación")
                        }
                    } else {
                        _loginState.value = LoginState.Error("Respuesta vacía del servidor")
                    }
                } else {
                    val code = response.code()
                    val errorBodyString = response.errorBody()?.string() ?: ""
                    
                    if (code == 403) {
                        // El backend respondió 403 Forbidden vacío; es el comportamiento base de Spring 
                        // cuando el usuario no pasa la pre-autenticación (cuenta inactiva/bloqueada)
                        _loginState.value = LoginState.Error("El usuario se encuentra inactivo")
                    } else {
                        var errorMessage = "Usuario o contraseña incorrectos"
                        try {
                            val json = org.json.JSONObject(errorBodyString)
                            val msg = json.optString("message", "")
                            if (msg.isNotEmpty()) {
                                errorMessage = msg
                            }
                        } catch (e: Exception) {
                            // Si no hay JSON válido, se queda el mensaje por defecto
                        }
                        _loginState.value = LoginState.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private suspend fun fetchAndSaveUserProfile(username: String) {
        try {
            val userResponse = RetrofitClient.userApi.getUserByUsername(username)
            if (userResponse.isSuccessful) {
                val userBody = userResponse.body()
                if (userBody != null) {
                    val userData = userBody["data"] as? Map<*, *>
                    if (userData != null) {
                        val id = (userData["id"] as? Double)?.toLong() ?: -1L
                        val uname = userData["username"]?.toString() ?: username
                        val email = userData["email"]?.toString() ?: ""
                        val municipality = userData["municipality"]?.toString() ?: ""
                        val role = userData["role"]?.toString() ?: "CITIZEN"
                        val isVolunteer = userData["isVolunteer"] as? Boolean ?: false
                        SessionManager.saveUserData(id, uname, email, municipality, role, isVolunteer)
                    }
                }
            }
        } catch (_: Exception) {
            // If profile fetch fails, we still have the token and username from JWT
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}