package mx.edu.utez.fmc_mobile.data.remote.dto.request

data class VerifyResetCodeRequest(
    val email: String,
    val code: String
)