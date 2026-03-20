package mx.edu.utez.fmc_mobile.data.remote.dto.request

data class CloseReportRequest(
    val photoUrls: List<String>,
    val comments: String? = null
)
