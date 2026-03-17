package mx.edu.utez.fmc_mobile.data.remote

import mx.edu.utez.fmc_mobile.data.local.AuthTokenStore
import mx.edu.utez.fmc_mobile.data.remote.service.AuthApiService
import mx.edu.utez.fmc_mobile.data.remote.service.ReportApiService
import mx.edu.utez.fmc_mobile.data.remote.service.SquadApiService
import mx.edu.utez.fmc_mobile.data.remote.service.UserApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = AuthTokenStore.token
            val request = if (!token.isNullOrBlank()) {
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }
        .build()

    private val instance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApiService = instance.create(AuthApiService::class.java)
    val reportApi: ReportApiService = instance.create(ReportApiService::class.java)
    val squadApi: SquadApiService = instance.create(SquadApiService::class.java)
    val userApi: UserApiService = instance.create(UserApiService::class.java)
}