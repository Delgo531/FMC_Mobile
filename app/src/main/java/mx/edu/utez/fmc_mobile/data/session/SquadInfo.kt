package mx.edu.utez.fmc_mobile.data.session

/**
 * Modelo de cuadrilla para la UI (alineado con backend SquadResponse).
 * Cuando exista GET /api/squads/me (o similar), mapear la respuesta aquí.
 */
data class SquadInfo(
    val id: Long,
    val name: String,
    val municipality: String,
    val description: String?,
    val status: SquadStatus,
    val members: List<SquadMemberInfo>,
    val pendingCount: Int = 0,  // reportes pendientes (cuando el backend lo exponga)
    val resolvedCount: Int = 0  // reportes resueltos (cuando el backend lo exponga)
)

data class SquadMemberInfo(
    val userId: Long,
    val username: String,
    val role: SquadRole
)
