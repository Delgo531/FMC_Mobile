package mx.edu.utez.fmc_mobile.utils

fun formatApiDate(raw: String): String {
    return try {
        val (datePart, timePart) = raw.split("T")
        val (y, m, d) = datePart.split("-")
        val hhmm = timePart.substring(0, 5)
        "$d-$m-$y - $hhmm"
    } catch (e: Exception) { raw }
}