package mx.edu.utez.fmc_mobile.data.remote.dto.request

data class UpdateUserRequest(
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String,
    val password: String? = null,
    val municipality: String
)