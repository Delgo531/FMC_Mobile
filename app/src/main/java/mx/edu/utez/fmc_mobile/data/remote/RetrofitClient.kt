package mx.edu.utez.fmc_mobile.data.remote

import mx.edu.utez.fmc_mobile.data.remote.service.ApplicationApiService
import mx.edu.utez.fmc_mobile.data.remote.service.AuthApiService
import mx.edu.utez.fmc_mobile.data.remote.service.NotificationApiService
import mx.edu.utez.fmc_mobile.data.remote.service.ReportApiService
import mx.edu.utez.fmc_mobile.data.remote.service.ReportAssignmentApiService
import mx.edu.utez.fmc_mobile.data.remote.service.SquadApiService
import mx.edu.utez.fmc_mobile.data.remote.service.UserApiService
import mx.edu.utez.fmc_mobile.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  RetrofitClient {
    private const val BASE_URL = "https://apifmc-production.up.railway.app/"

    private val authInterceptor = Interceptor { chain ->
        val token = SessionManager.getToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        val response = chain.proceed(request)
        // Si el token expiró (401) y había sesión activa, cerrar sesión automáticamente
        if (response.code == 401 && token != null) {
            SessionManager.onSessionExpired()
        }
        response
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val instance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApiService = instance.create(AuthApiService::class.java)
    val reportApi: ReportApiService = instance.create(ReportApiService::class.java)
    val squadApi: SquadApiService = instance.create(SquadApiService::class.java)
    val userApi: UserApiService = instance.create(UserApiService::class.java)
    val notificationApi: NotificationApiService = instance.create(NotificationApiService::class.java)
    val applicationApi: ApplicationApiService = instance.create(ApplicationApiService::class.java)
    val reportAssignmentApi: ReportAssignmentApiService = instance.create(ReportAssignmentApiService::class.java)
}