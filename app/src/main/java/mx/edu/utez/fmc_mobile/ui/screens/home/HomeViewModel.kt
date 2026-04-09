package mx.edu.utez.fmc_mobile.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.dto.response.ReportResponse
import mx.edu.utez.fmc_mobile.data.repository.ReportRepository
import mx.edu.utez.fmc_mobile.utils.NotificationHelper
import mx.edu.utez.fmc_mobile.utils.SessionManager

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReportRepository()
    private val gson = Gson()

    private val _reports = MutableStateFlow<List<ReportResponse>>(emptyList())
    val reports: StateFlow<List<ReportResponse>> = _reports

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _municipality = MutableStateFlow(SessionManager.getMunicipality())
    val municipality: StateFlow<String> = _municipality

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val municipality = SessionManager.getMunicipality()
                _municipality.value = municipality
                val response = if (municipality.isNotBlank()) {
                    repository.getReportsByMunicipality(municipality)
                } else {
                    repository.getAllReports()
                }
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.get("data")
                    if (data != null) {
                        val json = gson.toJson(data)
                        val type = object : TypeToken<List<ReportResponse>>() {}.type
                        val listJson = if (json.trimStart().startsWith("[")) json
                        else {
                            val pageMap = gson.fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
                            gson.toJson(pageMap["content"])
                        }
                        _reports.value = gson.fromJson(listJson, type) ?: emptyList()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al cargar reportes (Código: ${response.code()})"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }

            // Verificar y mostrar notificaciones nuevas en la barra del sistema.
            // El historial se persiste en SharedPreferences: si el usuario borra
            // la notificación de su panel, no vuelve a aparecer.
            NotificationHelper.pollAndShowNew(getApplication())
        }
    }
}
