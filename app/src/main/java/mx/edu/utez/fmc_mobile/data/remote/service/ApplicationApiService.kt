package mx.edu.utez.fmc_mobile.data.remote.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface ApplicationApiService {

    @POST("api/applications/volunteer")
    suspend fun applyAsVolunteer(): Response<Map<String, Any>>

    @GET("api/applications")
    suspend fun getAllApplications(): Response<Map<String, Any>>

    @GET("api/applications/pending")
    suspend fun getPendingApplications(): Response<Map<String, Any>>
}
