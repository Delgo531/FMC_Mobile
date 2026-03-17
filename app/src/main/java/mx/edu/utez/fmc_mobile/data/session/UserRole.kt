package mx.edu.utez.fmc_mobile.data.session

/**
 * Rol del usuario según el backend (entity.enums.Role).
 * CITIZEN = ciudadano; si además tiene cuadrilla asignada es miembro o líder.
 */
enum class UserRole {
    SUPER_ADMIN,
    ADMIN,
    CITIZEN
}
