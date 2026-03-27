package mx.edu.utez.fmc_mobile.data.remote.dto.request

import java.math.BigDecimal

data class CreateReportRequest(
    val title: String,
    val description: String,
    val address: String,
    val municipality: String,
    val latitude: BigDecimal = BigDecimal.ZERO,
    val longitude: BigDecimal = BigDecimal.ZERO,
    val photos: List<String>? = null
)