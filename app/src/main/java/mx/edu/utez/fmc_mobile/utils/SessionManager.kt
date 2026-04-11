package mx.edu.utez.fmc_mobile.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import kotlinx.coroutines.flow.MutableSharedFlow
import org.json.JSONObject

object SessionManager {

    private const val PREF_NAME = "fmc_session"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_EMAIL = "email"
    private const val KEY_MUNICIPALITY = "municipality"
    private const val KEY_ROLE = "role"
    private const val KEY_IS_VOLUNTEER = "is_volunteer"
    private const val KEY_HAS_PENDING_APPLICATION        = "has_pending_application"
    private const val KEY_HAS_PENDING_LEADER_APPLICATION = "has_pending_leader_application"
    private const val KEY_SHOWN_NOTIFICATION_IDS         = "shown_notification_ids"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        // Reset userId so pendingKey() doesn't reuse a stale ID from a previous account
        // until saveUserData() is called with the real userId after profile fetch
        prefs.edit().putString(KEY_TOKEN, token).putLong(KEY_USER_ID, -1L).apply()
        // Decode JWT payload to extract user info
        try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
                val json = JSONObject(payload)
                if (json.has("sub")) {
                    prefs.edit().putString(KEY_USERNAME, json.getString("sub")).apply()
                }
            }
        } catch (_: Exception) { }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUserData(id: Long, username: String, email: String, municipality: String, role: String = "CITIZEN", isVolunteer: Boolean = false) {
        prefs.edit()
            .putLong(KEY_USER_ID, id)
            .putString(KEY_USERNAME, username)
            .putString(KEY_EMAIL, email)
            .putString(KEY_MUNICIPALITY, municipality)
            .putString(KEY_ROLE, role)
            .putBoolean(KEY_IS_VOLUNTEER, isVolunteer)
            .apply()
    }

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1L)
    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""
    fun getEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""
    fun getMunicipality(): String = prefs.getString(KEY_MUNICIPALITY, "") ?: ""
    fun getRole(): String = prefs.getString(KEY_ROLE, "CITIZEN") ?: "CITIZEN"
    fun isVolunteer(): Boolean = prefs.getBoolean(KEY_IS_VOLUNTEER, false)

    // Keyed by userId so different accounts on the same device don't share the flag
    private fun pendingKey(): String = "${KEY_HAS_PENDING_APPLICATION}_${getUserId()}"

    fun hasPendingApplication(): Boolean = prefs.getBoolean(pendingKey(), false)
    fun setPendingApplication(value: Boolean) {
        prefs.edit().putBoolean(pendingKey(), value).apply()
    }

    private fun leaderPendingKey(): String = "${KEY_HAS_PENDING_LEADER_APPLICATION}_${getUserId()}"
    fun hasPendingLeaderApplication(): Boolean = prefs.getBoolean(leaderPendingKey(), false)
    fun setPendingLeaderApplication(value: Boolean) {
        prefs.edit().putBoolean(leaderPendingKey(), value).apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null

    // Keyed by userId so different accounts don't share shown-notification state
    private fun shownKey(): String = "${KEY_SHOWN_NOTIFICATION_IDS}_${getUserId()}"

    fun isNotificationShown(id: Long): Boolean =
        prefs.getStringSet(shownKey(), emptySet())?.contains(id.toString()) == true

    fun markNotificationShown(id: Long) {
        val current = prefs.getStringSet(shownKey(), emptySet())?.toMutableSet() ?: mutableSetOf()
        current.add(id.toString())
        prefs.edit().putStringSet(shownKey(), current).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    /** Emite un evento cuando el token expira (401). Observado por AppNavigation para redirigir al login. */
    val sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun onSessionExpired() {
        clearSession()
        sessionExpiredEvent.tryEmit(Unit)
    }
}
