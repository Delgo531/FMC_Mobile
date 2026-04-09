package mx.edu.utez.fmc_mobile.data.remote.service

import mx.edu.utez.fmc_mobile.data.remote.dto.request.LeaderApplicationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApplicationApiService {

    @POST("api/applications/volunteer")
    suspend fun applyAsVolunteer(): Response<Map<String, Any>>

    @POST("api/applications/leader")
    suspend fun applyAsLeader(@Body request: LeaderApplicationRequest): Response<Map<String, Any>>

    @GET("api/applications")
    suspend fun getAllApplications(): Response<Map<String, Any>>

    @GET("api/applications/pending")
    suspend fun getPendingApplications(): Response<Map<String, Any>>

    @GET("api/applications/my-status")
    suspend fun getMyApplicationStatus(): Response<Map<String, Any>>
}
