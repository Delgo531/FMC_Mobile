package mx.edu.utez.fmc_mobile.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateUserRequest
import mx.edu.utez.fmc_mobile.data.repository.AuthRepository
import mx.edu.utez.fmc_mobile.data.repository.UserRepository
import mx.edu.utez.fmc_mobile.utils.SessionManager

class ProfileViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    private val _username = MutableStateFlow(SessionManager.getUsername())
    val username: StateFlow<String> = _username

    private val _email = MutableStateFlow(SessionManager.getEmail())
    val email: StateFlow<String> = _email

    private val _municipality = MutableStateFlow(SessionManager.getMunicipality())
    val municipality: StateFlow<String> = _municipality

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    private val _updateState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateState: StateFlow<UpdateProfileState> = _updateState

    init {
        refreshProfile()
    }

    fun refreshProfile() {
        _username.value = SessionManager.getUsername()
        _email.value = SessionManager.getEmail()
        _municipality.value = SessionManager.getMunicipality()
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading
            try {
                authRepository.logout()
            } catch (_: Exception) { }
            SessionManager.clearSession()
            _logoutState.value = LogoutState.Success
        }
    }

    fun updateProfile(username: String, email: String, password: String, municipality: String) {
        if (username.isBlank()) {
            _updateState.value = UpdateProfileState.Error("El nombre de usuario es obligatorio")
            return
        }
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _updateState.value = UpdateProfileState.Error("Ingresa un correo válido")
            return
        }
        if (password.isBlank()) {
            _updateState.value = UpdateProfileState.Error("La contraseña es obligatoria para confirmar cambios")
            return
        }
        if (municipality.isBlank()) {
            _updateState.value = UpdateProfileState.Error("Selecciona un municipio")
            return
        }

        viewModelScope.launch {
            _updateState.value = UpdateProfileState.Loading
            try {
                val userId = SessionManager.getUserId()
                val request = UpdateUserRequest(
                    username = username,
                    email = email,
                    password = password,
                    municipality = municipality
                )
                val response = userRepository.updateUser(userId, request)
                if (response.isSuccessful) {
                    // Update session data
                    SessionManager.saveUserData(
                        id = userId,
                        username = username,
                        email = email,
                        municipality = municipality,
                        role = SessionManager.getRole(),
                        isVolunteer = SessionManager.isVolunteer()
                    )
                    refreshProfile()
                    _updateState.value = UpdateProfileState.Success
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al actualizar el perfil"
                    }
                    _updateState.value = UpdateProfileState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _updateState.value = UpdateProfileState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdateProfileState.Idle
    }
}

sealed class LogoutState {
    object Idle : LogoutState()
    object Loading : LogoutState()
    object Success : LogoutState()
}

sealed class UpdateProfileState {
    object Idle : UpdateProfileState()
    object Loading : UpdateProfileState()
    object Success : UpdateProfileState()
    data class Error(val message: String) : UpdateProfileState()
}
