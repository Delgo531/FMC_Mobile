package mx.edu.utez.fmc_mobile.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import mx.edu.utez.fmc_mobile.utils.Constants

object LocationHelper {

    /** true si la dirección contiene algún municipio de Morelos o el estado. */
    fun isInMorelos(address: String): Boolean {
        val lower = address.lowercase()
        return lower.contains("morelos")
            || lower.contains(", mor.")
            || lower.contains(" mor.")
            || Constants.municipiosMorelos.any { lower.contains(it.lowercase()) }
    }

    /**
     * Busca en el texto de la dirección un municipio de la lista oficial.
     * Devuelve el nombre exacto tal como aparece en Constants.municipiosMorelos,
     * o null si no hay coincidencia.
     */
    fun extractMunicipality(address: String): String? {
        val lower = address.lowercase()
        return Constants.municipiosMorelos.firstOrNull { municipio ->
            lower.contains(municipio.lowercase())
        }
    }

    /**
     * Obtiene la ubicación GPS y la convierte a dirección + municipio.
     * Primero intenta lastLocation (rápido); si es null, solicita una ubicación fresca.
     */
    suspend fun getAddressFromGps(context: Context): LocationResult =
        withContext(Dispatchers.IO) {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)

                val lastLocation = suspendCancellableCoroutine { cont ->
                    fusedClient.lastLocation
                        .addOnSuccessListener { cont.resume(it) }
                        .addOnFailureListener { cont.resume(null) }
                }

                val location = lastLocation ?: run {
                    val cts = CancellationTokenSource()
                    suspendCancellableCoroutine { cont ->
                        fusedClient
                            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                            .addOnSuccessListener { cont.resume(it) }
                            .addOnFailureListener { cont.resume(null) }
                        cont.invokeOnCancellation { cts.cancel() }
                    }
                } ?: return@withContext LocationResult.Error(
                    "No se pudo obtener la ubicación. Asegúrate de tener el GPS activado."
                )

                val info = reverseGeocode(context, location.latitude, location.longitude)
                    ?: return@withContext LocationResult.Error(
                        "No se pudo convertir la ubicación a dirección. Verifica tu conexión."
                    )

                LocationResult.Success(
                    address      = info.fullAddress,
                    municipality = info.municipality,
                    street       = info.street,
                    colony       = info.colony,
                    postalCode   = info.postalCode,
                    latitude     = location.latitude,
                    longitude    = location.longitude
                )

            } catch (_: SecurityException) {
                LocationResult.Error("Permisos de ubicación denegados.")
            } catch (e: Exception) {
                LocationResult.Error(e.message ?: "Error al obtener la ubicación.")
            }
        }

    // ── Geocoder interno ───────────────────────────────────────────────────────

    private data class AddressInfo(
        val fullAddress: String,
        val municipality: String,
        val street: String,
        val colony: String,
        val postalCode: String
    )

    private suspend fun reverseGeocode(context: Context, lat: Double, lng: Double): AddressInfo? =
        withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale("es", "MX"))
            val androidAddress: Address? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(lat, lng, 1) { addresses ->
                            cont.resume(addresses.firstOrNull())
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(lat, lng, 1)?.firstOrNull()
                }

            androidAddress ?: return@withContext null

            val fullAddress = androidAddress.getAddressLine(0) ?: return@withContext null

            // Candidatos del Geocoder para el municipio (en orden de fiabilidad en México)
            val candidates = listOfNotNull(
                androidAddress.subAdminArea,  // suele ser el municipio en México
                androidAddress.locality,       // a veces el municipio, a veces la ciudad
                androidAddress.subLocality     // raramente, pero por si acaso
            )
            // Busca el primer candidato que coincida con la lista oficial de municipios
            val municipality = candidates
                .firstNotNullOfOrNull { candidate ->
                    Constants.municipiosMorelos.firstOrNull { municipio ->
                        municipio.equals(candidate, ignoreCase = true) ||
                        candidate.contains(municipio, ignoreCase = true) ||
                        municipio.contains(candidate, ignoreCase = true)
                    }
                }
                ?: extractMunicipality(fullAddress)  // búsqueda en texto completo como último recurso
                ?: ""                                 // vacío → el usuario elige del dropdown

            val street = buildString {
                androidAddress.thoroughfare?.let { append(it) }
                androidAddress.subThoroughfare?.let { append(" $it") }
            }.trim()

            // subLocality es el campo correcto para colonia/barrio en México
            val colony     = androidAddress.subLocality ?: ""
            val postalCode = androidAddress.postalCode  ?: ""

            AddressInfo(fullAddress, municipality, street, colony, postalCode)
        }
}

sealed class LocationResult {
    data class Success(
        val address: String,
        val municipality: String,
        val street: String,
        val colony: String,
        val postalCode: String,
        val latitude: Double,
        val longitude: Double
    ) : LocationResult()

    data class Error(val message: String) : LocationResult()
}
