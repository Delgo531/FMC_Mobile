package mx.edu.utez.fmc_mobile.data.remote.dto.request

data class AdminRegisterRequest(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
    val municipality: String,
    val moduleIds: List<Long>
)