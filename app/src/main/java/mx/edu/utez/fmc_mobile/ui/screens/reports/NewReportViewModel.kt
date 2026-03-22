package mx.edu.utez.fmc_mobile.ui.screens.reports

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.dto.request.CreateReportRequest
import mx.edu.utez.fmc_mobile.data.repository.ReportRepository
import mx.edu.utez.fmc_mobile.utils.CloudinaryHelper
import mx.edu.utez.fmc_mobile.utils.SessionManager
import java.math.BigDecimal

class NewReportViewModel : ViewModel() {

    private val repository = ReportRepository()

    private val _createState = MutableStateFlow<CreateReportState>(CreateReportState.Idle)
    val createState: StateFlow<CreateReportState> = _createState

    fun createReport(
        context: Context,
        title: String,
        description: String,
        address: String,
        images: List<Uri>
    ) {
        if (title.isBlank() || title.length < 5) {
            _createState.value = CreateReportState.Error("El título debe tener al menos 5 caracteres")
            return
        }
        if (description.isBlank() || description.length < 20) {
            _createState.value = CreateReportState.Error("La descripción debe tener al menos 20 caracteres")
            return
        }
        if (address.isBlank()) {
            _createState.value = CreateReportState.Error("La dirección es obligatoria")
            return
        }

        viewModelScope.launch {
            _createState.value = CreateReportState.Loading("Subiendo imágenes...")
            try {
                // Upload images to Cloudinary
                val photoUrls = if (images.isNotEmpty()) {
                    val urls = CloudinaryHelper.uploadImages(context, images)
                    if (urls == null) {
                        _createState.value = CreateReportState.Error("Error al subir las imágenes")
                        return@launch
                    }
                    urls
                } else {
                    emptyList()
                }

                _createState.value = CreateReportState.Loading("Enviando reporte...")

                val municipality = SessionManager.getMunicipality()

                val request = CreateReportRequest(
                    title = title,
                    description = description,
                    address = address,
                    municipality = municipality,
                    latitude = BigDecimal.ZERO,
                    longitude = BigDecimal.ZERO,
                    photos = photoUrls.ifEmpty { null }
                )

                val response = repository.createReport(request)
                if (response.isSuccessful) {
                    _createState.value = CreateReportState.Success
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al crear el reporte (Código: ${response.code()})"
                    }
                    _createState.value = CreateReportState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _createState.value = CreateReportState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _createState.value = CreateReportState.Idle
    }
}

sealed class CreateReportState {
    object Idle : CreateReportState()
    data class Loading(val message: String) : CreateReportState()
    object Success : CreateReportState()
    data class Error(val message: String) : CreateReportState()
}
