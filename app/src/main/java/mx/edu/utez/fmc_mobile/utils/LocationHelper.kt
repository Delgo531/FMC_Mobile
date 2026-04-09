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

object LocationHelper {

    /**
     * Mapa de keyword (minúsculas, con/sin acentos) → nombre canónico del municipio.
     * Se usa para:
     *  - validar que la dirección está en Morelos (isInMorelos)
     *  - extraer el municipio correcto para enviarlo al API (extractMunicipality)
     */
    private val municipalityMap: Map<String, String> = mapOf(
        "cuernavaca"            to "Cuernavaca",
        "jiutepec"              to "Jiutepec",
        "temixco"               to "Temixco",
        "xochitepec"            to "Xochitepec",
        "emiliano zapata"       to "Emiliano Zapata",
        "tlaltizapán"           to "Tlaltizapán de Rayón",
        "tlaltizapan"           to "Tlaltizapán de Rayón",
        "tlaquiltenango"        to "Tlaquiltenango",
        "jojutla"               to "Jojutla",
        "puente de ixtla"       to "Puente de Ixtla",
        "yautepec"              to "Yautepec",
        "cuautla"               to "Cuautla",
        "yecapixtla"            to "Yecapixtla",
        "ayala"                 to "Ayala",
        "tepalcingo"            to "Tepalcingo",
        "axochiapan"            to "Axochiapan",
        "amacuzac"              to "Amacuzac",
        "coatlán del río"       to "Coatlán del Río",
        "coatlan del rio"       to "Coatlán del Río",
        "mazatepec"             to "Mazatepec",
        "tetecala"              to "Tetecala",
        "miacatlán"             to "Miacatlán",
        "miacatlan"             to "Miacatlán",
        "jonacatepec"           to "Jonacatepec de Leandro Valle",
        "tepoztlán"             to "Tepoztlán",
        "tepoztlan"             to "Tepoztlán",
        "totolapan"             to "Totolapan",
        "atlatlahucan"          to "Atlatlahucan",
        "ocuituco"              to "Ocuituco",
        "tetela del volcán"     to "Tetela del Volcán",
        "tetela del volcan"     to "Tetela del Volcán",
        "zacualpan de amilpas"  to "Zacualpan de Amilpas",
        "huitzilac"             to "Huitzilac",
        "temoac"                to "Temoac",
        "tlalnepantla"          to "Tlalnepantla"
    )

    /** true si la dirección contiene el estado o algún municipio de Morelos. */
    fun isInMorelos(address: String): Boolean {
        val lower = address.lowercase()
        return lower.contains("morelos")
            || lower.contains(", mor.")
            || lower.contains(" mor.")
            || municipalityMap.keys.any { lower.contains(it) }
    }

    /**
     * Extrae el nombre canónico del municipio buscando en el texto de la dirección.
     * Devuelve null si no coincide con ningún municipio conocido.
     */
    fun extractMunicipality(address: String): String? {
        val lower = address.lowercase()
        return municipalityMap.entries
            .firstOrNull { lower.contains(it.key) }
            ?.value
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

    private data class AddressInfo(val fullAddress: String, val municipality: String)

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

            // En México el Geocoder devuelve el municipio en subAdminArea o locality.
            // Si ninguno coincide con la lista, se extrae del texto completo.
            val municipality =
                androidAddress.subAdminArea?.let { extractMunicipality(it) }
                    ?: androidAddress.locality?.let { extractMunicipality(it) }
                    ?: extractMunicipality(fullAddress)
                    ?: "Morelos"   // fallback genérico si el geocoder no tiene datos precisos

            AddressInfo(fullAddress, municipality)
        }
}

sealed class LocationResult {
    data class Success(
        val address: String,
        val municipality: String,
        val latitude: Double,
        val longitude: Double
    ) : LocationResult()

    data class Error(val message: String) : LocationResult()
}
