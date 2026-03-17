package mx.edu.utez.fmc_mobile.data.remote.dto.response

import java.math.BigDecimal

data class AssignedReportResponse(
    val reportId: Long,
    val title: String,
    val description: String,
    val address: String,
    val municipality: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val reportStatus: String,
    val citizenId: Long,
    val citizenUsername: String,
    val photos: List<ReportPhotoResponse>,
    val reportCreatedAt: String,
    val assignmentId: Long,
    val assignmentStatus: String,
    val assignedAt: String
)