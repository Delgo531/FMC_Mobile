package mx.edu.utez.fmc_mobile.data.remote.dto.request

data class UpdateReportStatusRequest(
    val status: String,
    val notes: String? = null
)