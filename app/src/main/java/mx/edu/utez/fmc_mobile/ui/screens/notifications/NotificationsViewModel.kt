package mx.edu.utez.fmc_mobile.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.repository.NotificationRepository

data class NotificationItem(
    val id: Long,
    val type: String,
    val message: String,
    val read: Boolean,
    val referenceId: Long?,
    val referenceType: String?,
    val createdAt: String
)

class NotificationsViewModel : ViewModel() {

    private val repository = NotificationRepository()
    private val gson = Gson()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getMyNotifications()
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.get("data")
                    if (data != null) {
                        val json = gson.toJson(data)
                        val type = object : TypeToken<List<NotificationItem>>() {}.type
                        _notifications.value = gson.fromJson(json, type) ?: emptyList()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al cargar notificaciones"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            try {
                val response = repository.markAsRead(id)
                if (response.isSuccessful) {
                    _notifications.value = _notifications.value.map {
                        if (it.id == id) it.copy(read = true) else it
                    }
                }
            } catch (_: Exception) { }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val response = repository.markAllAsRead()
                if (response.isSuccessful) {
                    _notifications.value = _notifications.value.map { it.copy(read = true) }
                }
            } catch (_: Exception) { }
        }
    }
}
