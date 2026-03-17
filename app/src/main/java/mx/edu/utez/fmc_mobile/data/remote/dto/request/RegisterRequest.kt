package mx.edu.utez.fmc_mobile.data.remote.dto.request

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val municipality: String
)
