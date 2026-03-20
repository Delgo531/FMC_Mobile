package mx.edu.utez.fmc_mobile.data.remote.service

import mx.edu.utez.fmc_mobile.data.remote.dto.request.CreateSquadRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.LeaveSquadRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface SquadApiService {

    @POST("api/squads")
    suspend fun createSquad(@Body request: CreateSquadRequest): Response<Map<String, Any>>

    @GET("api/squads")
    suspend fun getAllSquads(): Response<Map<String, Any>>

    @PATCH("api/squads/leave")
    suspend fun leaveSquad(@Body request: LeaveSquadRequest): Response<Map<String, Any>>
}