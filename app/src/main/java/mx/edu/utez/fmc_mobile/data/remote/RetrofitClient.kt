package mx.edu.utez.fmc_mobile.data.remote

import mx.edu.utez.fmc_mobile.data.remote.service.AuthApiService
import mx.edu.utez.fmc_mobile.data.remote.service.ReportApiService
import mx.edu.utez.fmc_mobile.data.remote.service.SquadApiService
import mx.edu.utez.fmc_mobile.data.remote.service.UserApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val instance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApiService = instance.create(AuthApiService::class.java)
    val reportApi: ReportApiService = instance.create(ReportApiService::class.java)
    val squadApi: SquadApiService = instance.create(SquadApiService::class.java)
    val userApi: UserApiService = instance.create(UserApiService::class.java)
}