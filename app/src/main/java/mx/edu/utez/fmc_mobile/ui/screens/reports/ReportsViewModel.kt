package mx.edu.utez.fmc_mobile.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.dto.response.ReportResponse
import mx.edu.utez.fmc_mobile.data.repository.ReportRepository

class ReportsViewModel : ViewModel() {

    private val repository = ReportRepository()
    private val gson = Gson()

    private val _reports = MutableStateFlow<List<ReportResponse>>(emptyList())
    val reports: StateFlow<List<ReportResponse>> = _reports

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadMyReports()
    }

    fun loadMyReports() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getMyReports()
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.get("data")
                    if (data != null) {
                        val json = gson.toJson(data)
                        val type = object : TypeToken<List<ReportResponse>>() {}.type
                        _reports.value = gson.fromJson(json, type) ?: emptyList()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al cargar reportes"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
