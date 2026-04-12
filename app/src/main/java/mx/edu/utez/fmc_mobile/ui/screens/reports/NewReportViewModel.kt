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
import java.math.BigDecimal

class NewReportViewModel : ViewModel() {

    private val repository = ReportRepository()

    private val _createState = MutableStateFlow<CreateReportState>(CreateReportState.Idle)
    val createState: StateFlow<CreateReportState> = _createState

    private val _titleError = MutableStateFlow(false)
    val titleError: StateFlow<Boolean> = _titleError

    private val _descriptionError = MutableStateFlow(false)
    val descriptionError: StateFlow<Boolean> = _descriptionError

    private val _locationError = MutableStateFlow(false)
    val locationError: StateFlow<Boolean> = _locationError

    private val _imagesError = MutableStateFlow(false)
    val imagesError: StateFlow<Boolean> = _imagesError

    private val _locationState = MutableStateFlow<LocationFetchState>(LocationFetchState.Idle)
    val locationState: StateFlow<LocationFetchState> = _locationState

    // Solo lat/lng se guardan en el ViewModel; los campos de texto son propiedad de la pantalla
    private var gpsLatitude: BigDecimal = BigDecimal.ZERO
    private var gpsLongitude: BigDecimal = BigDecimal.ZERO

    /** Obtiene la ubicación GPS y convierte a dirección textual. */
    fun fetchLocation(context: Context) {
        viewModelScope.launch {
            _locationState.value = LocationFetchState.Loading
            when (val result = LocationHelper.getAddressFromGps(context)) {
                is LocationResult.Success -> {
                    gpsLatitude  = BigDecimal.valueOf(result.latitude)
                    gpsLongitude = BigDecimal.valueOf(result.longitude)
                    _locationState.value = LocationFetchState.Success(
                        municipality = result.municipality,
                        colony       = result.colony,
                        street       = result.street,
                        postalCode   = result.postalCode
                    )
                }
                is LocationResult.Error -> {
                    _locationState.value = LocationFetchState.Error(result.message)
                }
            }
        }
    }

    fun resetLocationState() {
        _locationState.value = LocationFetchState.Idle
        gpsLatitude  = BigDecimal.ZERO
        gpsLongitude = BigDecimal.ZERO
    }

    fun createReport(
        context: Context,
        title: String,
        description: String,
        municipality: String,
        colony: String,
        street: String,
        postalCode: String,
        locationDetails: String,
        images: List<Uri>
    ) {
        // Validar todos los campos a la vez para marcar todos los errores simultáneamente
        _titleError.value       = title.isBlank() || title.length < 5
        _descriptionError.value = description.isBlank() || description.length < 20
        _locationError.value    = municipality.isBlank()
        _imagesError.value      = images.isEmpty()

        if (_titleError.value || _descriptionError.value || _locationError.value || _imagesError.value) {
            _createState.value = CreateReportState.Error(
                when {
                    _titleError.value       -> "El título debe tener al menos 5 caracteres"
                    _descriptionError.value -> "La descripción debe tener al menos 20 caracteres"
                    _locationError.value    -> "El municipio no puede estar vacío"
                    else                    -> "Debes agregar al menos una foto del problema"
                }
            )
            return
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

                val addressParts = listOfNotNull(
                    street.takeIf { it.isNotBlank() },
                    colony.takeIf { it.isNotBlank() },
                    municipality.takeIf { it.isNotBlank() },
                    postalCode.takeIf { it.isNotBlank() }
                )
                val address = addressParts.joinToString(", ")
                    .let { if (locationDetails.isNotBlank()) "$it - $locationDetails" else it }

                val request = CreateReportRequest(
                    title        = title,
                    description  = description,
                    address      = address,
                    municipality = municipality,
                    latitude     = gpsLatitude,
                    longitude    = gpsLongitude,
                    photos       = photoUrls.ifEmpty { null }
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

    fun clearTitleError()       { _titleError.value = false }
    fun clearDescriptionError() { _descriptionError.value = false }
    fun clearLocationError()    { _locationError.value = false }
    fun clearImagesError()      { _imagesError.value = false }

    fun resetState() {
        _createState.value = CreateReportState.Idle
        _titleError.value = false
        _descriptionError.value = false
        _locationError.value = false
        _imagesError.value = false
        gpsLatitude  = BigDecimal.ZERO
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
    data class Success(
        val municipality: String,
        val colony: String,
        val street: String,
        val postalCode: String
    ) : LocationFetchState()
    data class Error(val message: String) : LocationFetchState()
}
