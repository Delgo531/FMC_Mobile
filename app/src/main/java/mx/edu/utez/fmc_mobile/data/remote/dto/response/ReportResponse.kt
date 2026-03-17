package mx.edu.utez.fmc_mobile.data.remote.dto.response

import java.math.BigDecimal

data class ReportResponse(
    val id: Long,
    val title: String,
    val description: String,
    val address: String,
    val municipality: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val status: String,
    val citizenId: Long,
    val citizenUsername: String,
    val photos: List<ReportPhotoResponse>,
    val createdAt: String,
    val updatedAt: String
)