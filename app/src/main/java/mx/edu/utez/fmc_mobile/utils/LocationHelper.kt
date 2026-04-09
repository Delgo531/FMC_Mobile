package mx.edu.utez.fmc_mobile.utils

import android.content.Context
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

object LocationHelper {

    // Municipios de Morelos + nombre del estado para validar que la dirección sea local
    private val morelosKeywords = listOf(
        "morelos",
        "cuernavaca", "jiutepec", "temixco", "xochitepec", "emiliano zapata",
        "tlaltizapán", "tlaltizapan", "tlaquiltenango", "jojutla",
        "puente de ixtla", "yautepec", "cuautla", "yecapixtla", "ayala",
        "tepalcingo", "axochiapan", "amacuzac", "coatlán del río", "coatlan del rio",
        "mazatepec", "tetecala", "miacatlán", "miacatlan", "jonacatepec",
        "tepoztlán", "tepoztlan", "totolapan", "atlatlahucan", "ocuituco",
        "tetela del volcán", "tetela del volcan", "zacualpan de amilpas",
        "huitzilac", "temoac", "tlalnepantla"
    )

    /** Devuelve true si el texto de dirección contiene algún municipio o el estado de Morelos. */
    fun isInMorelos(address: String): Boolean =
        morelosKeywords.any { address.lowercase().contains(it) }

    /**
     * Obtiene la ubicación GPS actual y la convierte a dirección textual.
     * Primero intenta con la última ubicación conocida (rápido); si no hay,
     * solicita una ubicación fresca con alta precisión.
     */
    suspend fun getAddressFromGps(context: Context): LocationResult =
        withContext(Dispatchers.IO) {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)

                // Intenta ubicación reciente primero para respuesta más rápida
                val lastLocation = suspendCancellableCoroutine { cont ->
                    fusedClient.lastLocation
                        .addOnSuccessListener { cont.resume(it) }
                        .addOnFailureListener { cont.resume(null) }
                }

                val location = lastLocation ?: run {
                    // No hay ubicación reciente: solicita una fresca
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

                val address = reverseGeocode(context, location.latitude, location.longitude)
                    ?: return@withContext LocationResult.Error(
                        "No se pudo convertir la ubicación a dirección. Verifica tu conexión."
                    )

                LocationResult.Success(address, location.latitude, location.longitude)

            } catch (_: SecurityException) {
                LocationResult.Error("Permisos de ubicación denegados.")
            } catch (e: Exception) {
                LocationResult.Error(e.message ?: "Error al obtener la ubicación.")
            }
        }

    private suspend fun reverseGeocode(context: Context, lat: Double, lng: Double): String? =
        withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale("es", "MX"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(lat, lng, 1) { addresses ->
                        cont.resume(addresses.firstOrNull()?.getAddressLine(0))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(lat, lng, 1)?.firstOrNull()?.getAddressLine(0)
            }
        }
}

sealed class LocationResult {
    data class Success(
        val address: String,
        val latitude: Double,
        val longitude: Double
    ) : LocationResult()

    data class Error(val message: String) : LocationResult()
}
