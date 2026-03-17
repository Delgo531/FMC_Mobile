package mx.edu.utez.fmc_mobile.data.session

import androidx.compose.runtime.compositionLocalOf

/**
 * Sesión del usuario actual. Proporcionar en la raíz (p. ej. MainActivity o tras el login).
 * Valor por defecto: ciudadano sin cuadrilla. Cuando el login consuma la API,
 * actualizar con el role (y squad si existe) de la respuesta.
 */
val LocalUserSession = compositionLocalOf {
    UserSession(role = UserRole.CITIZEN, squad = null)
}
