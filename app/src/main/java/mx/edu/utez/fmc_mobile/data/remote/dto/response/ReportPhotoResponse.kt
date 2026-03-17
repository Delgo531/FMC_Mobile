package mx.edu.utez.fmc_mobile.data.remote.dto.response

data class ReportPhotoResponse(
    val id: Long,
    val filePath: String,
    val type: String,
    val createdAt: String
)