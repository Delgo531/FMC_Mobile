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
import mx.edu.utez.fmc_mobile.utils.LocationHelper
import mx.edu.utez.fmc_mobile.utils.LocationResult
import mx.edu.utez.fmc_mobile.utils.SessionManager
import java.math.BigDecimal

class NewReportViewModel : ViewModel() {

    private val repository = ReportRepository()

    private val _createState = MutableStateFlow<CreateReportState>(CreateReportState.Idle)
    val createState: StateFlow<CreateReportState> = _createState

    private val _locationState = MutableStateFlow<LocationFetchState>(LocationFetchState.Idle)
    val locationState: StateFlow<LocationFetchState> = _locationState

    // Coordenadas y municipio obtenidos por GPS — se usan al enviar el reporte
    private var gpsLatitude: BigDecimal = BigDecimal.ZERO
    private var gpsLongitude: BigDecimal = BigDecimal.ZERO
    private var resolvedMunicipality: String = ""

    /** Obtiene la ubicación GPS y convierte a dirección textual. */
    fun fetchLocation(context: Context) {
        viewModelScope.launch {
            _locationState.value = LocationFetchState.Loading
            when (val result = LocationHelper.getAddressFromGps(context)) {
                is LocationResult.Success -> {
                    gpsLatitude = BigDecimal.valueOf(result.latitude)
                    gpsLongitude = BigDecimal.valueOf(result.longitude)
                    resolvedMunicipality = result.municipality
                    _locationState.value = LocationFetchState.Success(result.address)
                }
                is LocationResult.Error -> {
                    _locationState.value = LocationFetchState.Error(result.message)
                }
            }
        }
    }

    fun resetLocationState() {
        _locationState.value = LocationFetchState.Idle
        resolvedMunicipality = ""
    }

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
        if (address.length < 10) {
            _createState.value = CreateReportState.Error("Ingresa una dirección más completa (calle, número y colonia)")
            return
        }
        val userMunicipality = SessionManager.getMunicipality()

        // Validar que la dirección GPS corresponda al municipio del usuario
        if (resolvedMunicipality.isNotBlank() && resolvedMunicipality != userMunicipality) {
            _createState.value = CreateReportState.Error(
                "Tu ubicación GPS está en $resolvedMunicipality. Solo puedes reportar en tu municipio: $userMunicipality"
            )
            return
        }

        // Validar que la dirección escrita manualmente corresponda al municipio del usuario
        if (resolvedMunicipality.isBlank()) {
            val extracted = LocationHelper.extractMunicipality(address)
            if (extracted != null && extracted != userMunicipality) {
                _createState.value = CreateReportState.Error(
                    "La dirección está en $extracted. Solo puedes reportar en tu municipio: $userMunicipality"
                )
                return
            }
            if (!LocationHelper.isInMorelos(address)) {
                _createState.value = CreateReportState.Error(
                    "La dirección debe estar en tu municipio de registro: $userMunicipality"
                )
                return
            }
        }

        viewModelScope.launch {
            _createState.value = CreateReportState.Loading("Subiendo imágenes...")
            try {
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

                // Siempre se envía el municipio de registro del usuario (regla de negocio)
                val municipality = userMunicipality

                val request = CreateReportRequest(
                    title = title,
                    description = description,
                    address = address,
                    municipality = municipality,
                    latitude = gpsLatitude,
                    longitude = gpsLongitude,
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
        resolvedMunicipality = ""
        gpsLatitude = BigDecimal.ZERO
        gpsLongitude = BigDecimal.ZERO
    }
}

sealed class CreateReportState {
    object Idle : CreateReportState()
    data class Loading(val message: String) : CreateReportState()
    object Success : CreateReportState()
    data class Error(val message: String) : CreateReportState()
}

sealed class LocationFetchState {
    object Idle : LocationFetchState()
    object Loading : LocationFetchState()
    data class Success(val address: String) : LocationFetchState()
    data class Error(val message: String) : LocationFetchState()
}
