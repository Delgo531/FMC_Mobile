package mx.edu.utez.fmc_mobile.data.remote.service

import mx.edu.utez.fmc_mobile.data.remote.dto.request.CreateSquadRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.response.AssignedReportResponse
import mx.edu.utez.fmc_mobile.data.remote.dto.response.SquadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SquadApiService {

    @POST("api/squads")
    suspend fun createSquad(@Body request: CreateSquadRequest): Response<SquadResponse>

    @GET("api/squads/assigned-reports")
    suspend fun getAssignedReports(): Response<List<AssignedReportResponse>>
}