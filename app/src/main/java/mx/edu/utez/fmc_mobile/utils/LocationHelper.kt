package mx.edu.utez.fmc_mobile.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.text.Normalizer
import java.util.Locale
import kotlin.coroutines.resume

object LocationHelper {

    /**
     * Mapa de prefijo de CP (3 dígitos) → municipio oficial.
     * Cubre los rangos postales del estado de Morelos (62000-62999).
     * Sirve como fallback cuando el Geocoder solo devuelve una localidad
     * menor (colonia/pueblo) que no coincide con ningún municipio.
     */
    private val postalPrefixToMunicipality = mapOf(
        "620" to "Cuernavaca",  "621" to "Cuernavaca",  "622" to "Cuernavaca",
        "623" to "Jiutepec",   "624" to "Jiutepec",
        "625" to "Temixco",
        "626" to "Emiliano Zapata",
        "627" to "Xochitepec",
        "628" to "Puente de Ixtla",
        "629" to "Jojutla",
        "630" to "Cuautla",    "631" to "Cuautla",
        "632" to "Yautepec",   "633" to "Yautepec",
        "634" to "Ayala",      "635" to "Ayala",
        "636" to "Yecapixtla", "637" to "Yecapixtla",
        "638" to "Jantetelco",
        "639" to "Axochiapan", "640" to "Axochiapan",
        "641" to "Axochiapan", "642" to "Axochiapan",
        "643" to "Amacuzac",   "644" to "Amacuzac",
        "645" to "Jojutla",    "646" to "Tlaquiltenango",
        "647" to "Zacatepec",  "648" to "Zacatepec",
        "649" to "Miacatlán",  "650" to "Miacatlán",
        "651" to "Mazatepec",
        "652" to "Tetecala",   "653" to "Tetecala",
        "654" to "Coatlán del Río",
        "655" to "Huitzilac",
        "656" to "Tepoztlán",  "657" to "Tepoztlán",
        "658" to "Totolapan",  "659" to "Atlatlahucan",
        "660" to "Ocuituco",   "661" to "Tetela del Volcán",
        "662" to "Temoac",
        "663" to "Tlalnepantla",
        "664" to "Tlayacapan"
    )

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
     * Usa comparación normalizada (sin acentos, sin mayúsculas) para mayor tolerancia.
     */
    fun extractMunicipality(address: String): String? {
        val norm = address.normalize()
        return Constants.municipiosMorelos.firstOrNull { municipio ->
            norm.contains(municipio.normalize())
        }
    }

    /** Quita acentos y pasa a minúsculas para comparaciones tolerantes. */
    private fun String.normalize(): String =
        Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .lowercase()

    /**
     * Obtiene la ubicación GPS y la convierte a dirección + municipio.
     *
     * Timeouts:
     *  - lastLocation   : 5 s  (suele ser instantáneo si el dispositivo ya usó GPS)
     *  - getCurrentLocation: 15 s (espera señal GPS fresca)
     *  - reverseGeocode : 8 s  (requiere internet para llamar al servicio de Google)
     */
    suspend fun getAddressFromGps(context: Context): LocationResult =
        withContext(Dispatchers.IO) {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)

                // ── Paso 1: última ubicación conocida (rápido, puede ser null) ──────
                val lastLocation = withTimeoutOrNull(5_000) {
                    suspendCancellableCoroutine { cont ->
                        fusedClient.lastLocation
                            .addOnSuccessListener { cont.resume(it) }
                            .addOnFailureListener { cont.resume(null) }
                    }
                }

                // ── Paso 2: si no hay cached, pide una ubicación fresca ─────────────
                val location = lastLocation
                    ?: withTimeoutOrNull(15_000) {
                        val cts = CancellationTokenSource()
                        suspendCancellableCoroutine { cont ->
                            fusedClient
                                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                                .addOnSuccessListener { cont.resume(it) }
                                .addOnFailureListener { cont.resume(null) }
                            cont.invokeOnCancellation { cts.cancel() }
                        }
                    }
                    ?: return@withContext LocationResult.Error(
                        "No se pudo obtener la ubicación. Asegúrate de tener el GPS activado y señal disponible."
                    )

                // ── Paso 3: geocodificación inversa con timeout ────────────────────
                val info = withTimeoutOrNull(8_000) {
                    reverseGeocode(context, location.latitude, location.longitude)
                } ?: return@withContext LocationResult.Error(
                    "No se pudo obtener la dirección. Verifica que tienes conexión a internet."
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
            try {
                val geocoder = Geocoder(context, Locale("es", "MX"))

                val androidAddress: Address? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        suspendCancellableCoroutine { cont ->
                            try {
                                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                                    cont.resume(addresses.firstOrNull())
                                }
                            } catch (_: Exception) { cont.resume(null) }
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        try { geocoder.getFromLocation(lat, lng, 1)?.firstOrNull() }
                        catch (_: Exception) { null }
                    }

                androidAddress ?: return@withContext null

                val fullAddress = androidAddress.getAddressLine(0) ?: return@withContext null
                val postalCode  = androidAddress.postalCode ?: ""

                // ── Log de diagnóstico ────────────────────────────────────────────
                Log.d("LocationHelper", "=== Geocoder campos ===")
                Log.d("LocationHelper", "fullAddress   : $fullAddress")
                Log.d("LocationHelper", "adminArea     : ${androidAddress.adminArea}")
                Log.d("LocationHelper", "subAdminArea  : ${androidAddress.subAdminArea}")
                Log.d("LocationHelper", "locality      : ${androidAddress.locality}")
                Log.d("LocationHelper", "subLocality   : ${androidAddress.subLocality}")
                Log.d("LocationHelper", "thoroughfare  : ${androidAddress.thoroughfare}")
                Log.d("LocationHelper", "postalCode    : ${androidAddress.postalCode}")

                // ── Municipio ────────────────────────────────────────────────────
                // Normaliza (sin acentos, minúsculas) para tolerar variantes del Geocoder.
                fun matchMunicipality(candidate: String): String? =
                    Constants.municipiosMorelos.firstOrNull { m ->
                        m.normalize() == candidate.normalize() ||
                        candidate.normalize().contains(m.normalize()) ||
                        m.normalize().contains(candidate.normalize())
                    }

                val candidates = listOfNotNull(
                    androidAddress.subAdminArea,
                    androidAddress.locality,
                    androidAddress.subLocality,
                    androidAddress.adminArea
                )

                val municipality =
                    // Intento 1: campos estructurados
                    candidates.firstNotNullOfOrNull { matchMunicipality(it) }
                    // Intento 2: ciudad del patrón "62270 Cuernavaca"
                    ?: run {
                        val parts = fullAddress.split(",").map { it.trim() }
                        val postalSegment = parts.firstOrNull { it.matches(Regex("\\d{5}.*")) }
                        postalSegment?.drop(5)?.trim()?.let { matchMunicipality(it) }
                    }
                    // Intento 3: búsqueda normalizada en todo el texto
                    ?: extractMunicipality(fullAddress)
                    // Intento 4: prefijo del código postal (ej. "625" → Temixco)
                    ?: postalCode.takeIf { it.length >= 3 }
                        ?.let { postalPrefixToMunicipality[it.take(3)] }
                    ?: ""

                Log.d("LocationHelper", "municipality  : $municipality")

                // ── Calle y colonia ──────────────────────────────────────────────
                // Los campos thoroughfare/subLocality frecuentemente son null en México.
                // Como fallback, parseamos getAddressLine(0).
                val structuredStreet = buildString {
                    androidAddress.thoroughfare?.let { append(it) }
                    androidAddress.subThoroughfare?.let { append(" $it") }
                }.trim()

                val structuredColony = androidAddress.subLocality ?: ""

                val (parsedStreet, parsedColony) = if (structuredStreet.isBlank() || structuredColony.isBlank()) {
                    parseStreetAndColony(fullAddress, postalCode)
                } else {
                    Pair(structuredStreet, structuredColony)
                }

                val street = structuredStreet.ifBlank { parsedStreet }
                val colony = structuredColony.ifBlank { parsedColony }

                AddressInfo(fullAddress, municipality, street, colony, postalCode)

            } catch (_: Exception) { null }
        }

    /**
     * Parsea "getAddressLine(0)" para extraer calle y colonia cuando los campos
     * estructurados del Geocoder vienen vacíos.
     *
     * Formato típico en México:
     *   "Av. Morelos 45, Lomas de Cortés, 62270 Cuernavaca, Mor., México"
     *    ─────────────  ───────────────  ───────────────────────────────
     *       calle             colonia              ruido (ignorar)
     */
    private fun parseStreetAndColony(fullAddress: String, postalCode: String): Pair<String, String> {
        val parts = fullAddress.split(",").map { it.trim() }.filter { it.isNotBlank() }

        // Identifica las partes que son "ruido": CP + ciudad, estado, país, municipio
        val noiseParts = parts.filter { part ->
            val lower = part.lowercase()
            (postalCode.isNotBlank() && lower.contains(postalCode)) ||
            lower.matches(Regex("\\d{5}.*")) ||
            lower == "méxico" || lower == "mexico" ||
            lower.startsWith("mor") ||
            Constants.municipiosMorelos.any { m ->
                part.equals(m, ignoreCase = true) ||
                part.contains(m, ignoreCase = true)
            }
        }.toSet()

        val meaningful = parts.filter { it !in noiseParts }

        // meaningful[0] = calle, meaningful[1] = colonia (si existe)
        // Si solo hay un segmento útil, intentamos ver si es colonia o calle:
        //  - Si empieza con prefijos de colonia → es colonia (calle vacía)
        //  - Si contiene dígitos o prefijos de vía → es calle (colonia vacía)
        val streetPrefixes = listOf("av.", "avenida", "blvd", "boulevard", "calz", "carretera",
            "calle", "priv", "andador", "paseo", "circuito", "fracc")
        val colonyPrefixes = listOf("col.", "colonia", "fracc.", "fraccionamiento", "lomas",
            "jardines", "residencial", "barrio", "unidad", "ampliación")

        return if (meaningful.size >= 2) {
            Pair(meaningful[0], meaningful[1])
        } else if (meaningful.size == 1) {
            val seg = meaningful[0]
            val lower = seg.lowercase()
            when {
                colonyPrefixes.any { lower.startsWith(it) } -> Pair("", seg)
                streetPrefixes.any { lower.startsWith(it) } -> Pair(seg, "")
                seg.any { it.isDigit() }                    -> Pair(seg, "")  // tiene número → probablemente calle
                else                                        -> Pair(seg, "")
            }
        } else {
            Pair("", "")
        }
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
