package mx.edu.utez.fmc_mobile.ui.screens.teams

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.dto.request.CloseReportRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateReportStatusRequest
import mx.edu.utez.fmc_mobile.data.repository.ReportAssignmentRepository
import mx.edu.utez.fmc_mobile.utils.CloudinaryHelper

class ReportDetailsViewModel : ViewModel() {

    private val repository = ReportAssignmentRepository()

    private val _actionState = MutableStateFlow<ReportActionState>(ReportActionState.Idle)
    val actionState: StateFlow<ReportActionState> = _actionState

    fun changeStatus(assignmentId: Long, status: String, notes: String?) {
        viewModelScope.launch {
            _actionState.value = ReportActionState.Loading
            try {
                val response = repository.changeReportStatus(
                    assignmentId,
                    UpdateReportStatusRequest(status, notes)
                )
                if (response.isSuccessful) {
                    _actionState.value = ReportActionState.Success("Estado actualizado")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al cambiar estado"
                    }
                    _actionState.value = ReportActionState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _actionState.value = ReportActionState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun closeWithEvidence(context: Context, assignmentId: Long, images: List<Uri>, comments: String?) {
        if (images.size != 3) {
            _actionState.value = ReportActionState.Error("Es obligatorio subir exactamente 3 fotos de evidencia")
            return
        }
        viewModelScope.launch {
            _actionState.value = ReportActionState.Loading
            try {
                // Upload images to Cloudinary
                val photoUrls = CloudinaryHelper.uploadImages(context, images)
                if (photoUrls == null) {
                    _actionState.value = ReportActionState.Error("Error al subir las imágenes")
                    return@launch
                }

                val response = repository.closeReportWithEvidence(
                    assignmentId,
                    CloseReportRequest(photoUrls = photoUrls, comments = comments)
                )
                if (response.isSuccessful) {
                    _actionState.value = ReportActionState.Success("Reporte cerrado exitosamente")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al cerrar reporte"
                    }
                    _actionState.value = ReportActionState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _actionState.value = ReportActionState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _actionState.value = ReportActionState.Idle
    }
}

sealed class ReportActionState {
    object Idle : ReportActionState()
    object Loading : ReportActionState()
    data class Success(val message: String) : ReportActionState()
    data class Error(val message: String) : ReportActionState()
}
