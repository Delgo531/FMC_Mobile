package mx.edu.utez.fmc_mobile.data.remote.dto.response

data class SquadResponse(
    val id: Long,
    val name: String,
    val municipality: String,
    val description: String,
    val status: String,
    val members: List<SquadMemberResponse>,
    val createdAt: String,
    val updatedAt: String
)

data class SquadMemberResponse(
    val userId: Long,
    val username: String,
    val role: String
)