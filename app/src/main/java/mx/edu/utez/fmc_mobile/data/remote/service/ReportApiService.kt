package mx.edu.utez.fmc_mobile.data.remote.service

import mx.edu.utez.fmc_mobile.data.remote.dto.request.CreateReportRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.response.ReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportApiService {

    @POST("api/reports")
    suspend fun createReport(@Body request: CreateReportRequest): Response<ReportResponse>

    @GET("api/reports/{id}")
    suspend fun getReportById(@Path("id") id: Long): Response<ReportResponse>

    @GET("api/reports")
    suspend fun getAllReports(): Response<List<ReportResponse>>
}