package mx.edu.utez.fmc_mobile.data.remote.dto.response

data class AdminUserResponse(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val municipality: String,
    val role: String,
    val status: String,
    val isVolunteer: Boolean,
    val createdAt: String
)