package mx.edu.utez.fmc_mobile.data.local

/**
 * Almacenamiento simple en memoria para el JWT.
 * Nota: si necesitas persistencia real, migrar a DataStore/SharedPreferences.
 */
object AuthTokenStore {
    @Volatile
    var token: String? = null
        private set

    fun setToken(newToken: String?) {
        token = newToken?.takeIf { it.isNotBlank() }
    }

    fun clear() {
        token = null
    }
}

