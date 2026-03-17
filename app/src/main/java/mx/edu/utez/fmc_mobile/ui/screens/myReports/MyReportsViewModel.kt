package mx.edu.utez.fmc_mobile.ui.screens.myReports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.dto.response.ReportResponse
import mx.edu.utez.fmc_mobile.data.repository.ReportRepository

sealed class MyReportsUiState {
    data object Idle : MyReportsUiState()
    data object Loading : MyReportsUiState()
    data class Loaded(val reports: List<ReportResponse>) : MyReportsUiState()
    data class Error(val message: String) : MyReportsUiState()
}

class MyReportsViewModel : ViewModel() {
    private val repository = ReportRepository()

    private val _state = MutableStateFlow<MyReportsUiState>(MyReportsUiState.Idle)
    val state: StateFlow<MyReportsUiState> = _state

    private val _selectedReport = MutableStateFlow<ReportResponse?>(null)
    val selectedReport: StateFlow<ReportResponse?> = _selectedReport

    fun load() {
        viewModelScope.launch {
            _state.value = MyReportsUiState.Loading
            try {
                val response = repository.getAllReports()
                if (response.isSuccessful) {
                    _state.value = MyReportsUiState.Loaded(response.body().orEmpty())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "No se pudieron cargar tus reportes"
                    }
                    _state.value = MyReportsUiState.Error(message)
                }
            } catch (e: Exception) {
                _state.value = MyReportsUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun openReport(report: ReportResponse) {
        _selectedReport.value = report
    }

    fun closeReportDialog() {
        _selectedReport.value = null
    }
}

