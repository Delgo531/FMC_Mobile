package mx.edu.utez.fmc_mobile.data.remote.dto.response

data class StandardResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)
