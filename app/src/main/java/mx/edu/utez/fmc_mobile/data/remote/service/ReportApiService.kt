package mx.edu.utez.fmc_mobile.data.remote.service

import mx.edu.utez.fmc_mobile.data.remote.dto.request.CreateReportRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportApiService {

    @POST("api/reports")
    suspend fun createReport(@Body request: CreateReportRequest): Response<Map<String, Any>>

    @GET("api/reports/{id}")
    suspend fun getReportById(@Path("id") id: Long): Response<Map<String, Any>>

    @GET("api/reports?sort=createdAt,desc")
    suspend fun getAllReports(): Response<Map<String, Any>>

    @GET("api/reports/my-reports")
    suspend fun getMyReports(): Response<Map<String, Any>>

    @GET("api/reports/municipality/{municipality}?sort=createdAt,desc")
    suspend fun getReportsByMunicipality(@Path("municipality") municipality: String): Response<Map<String, Any>>
}