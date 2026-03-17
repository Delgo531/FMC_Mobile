package mx.edu.utez.fmc_mobile.data.session

/**
 * Sesión del usuario actual. Se puede llenar al hacer login con la API
 * (AuthResponse podría incluir role; un endpoint GET /me podría devolver squad).
 * Por ahora se usa valor por defecto: CITIZEN sin cuadrilla.
 */
data class UserSession(
    val role: UserRole,
    val squad: SquadInfo? = null
) {
    /** true si el usuario es miembro o líder de una cuadrilla (debe verse la pantalla con datos). */
    val isInSquad: Boolean get() = squad != null
}
