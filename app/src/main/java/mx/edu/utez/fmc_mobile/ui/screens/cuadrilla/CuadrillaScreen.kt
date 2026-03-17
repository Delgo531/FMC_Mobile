package mx.edu.utez.fmc_mobile.ui.screens.cuadrilla

import androidx.compose.runtime.Composable
import mx.edu.utez.fmc_mobile.data.session.LocalUserSession
import mx.edu.utez.fmc_mobile.data.session.UserSession

/**
 * Pantalla de la pestaña Cuadrilla. Muestra una de dos versiones según el usuario:
 * - **Ciudadano sin cuadrilla**: [CitizenSquadContent] (info voluntarios + "¡Quiero ser voluntario!").
 * - **Miembro o líder**: [MemberLeaderSquadContent] (datos de la cuadrilla, integrantes, Pendientes/Resueltos).
 *
 * La sesión se lee de [LocalUserSession]. Por defecto es CITIZEN sin cuadrilla.
 * Cuando el login consuma la API, se puede actualizar la sesión con el role y la cuadrilla (si existe).
 */
@Composable
fun CuadrillaScreen(
    session: UserSession = LocalUserSession.current
) {
    when {
        session.isInSquad -> {
            MemberLeaderSquadContent(
                squad = session.squad!!,
                onAbandonClick = { /* TODO: cuando el backend exponga abandonar cuadrilla */ }
            )
        }
        else -> {
            CitizenSquadContent()
        }
    }
}
