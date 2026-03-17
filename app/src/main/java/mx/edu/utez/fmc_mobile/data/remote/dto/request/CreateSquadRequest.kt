package mx.edu.utez.fmc_mobile.data.remote.dto.request

data class CreateSquadRequest(
    val municipality: String,
    val description: String? = null,
    val usernames: List<String>? = null
)
