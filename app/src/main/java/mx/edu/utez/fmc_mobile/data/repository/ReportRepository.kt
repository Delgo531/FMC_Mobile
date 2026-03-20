package mx.edu.utez.fmc_mobile.data.repository

import mx.edu.utez.fmc_mobile.data.remote.RetrofitClient
import mx.edu.utez.fmc_mobile.data.remote.dto.request.CreateReportRequest

class ReportRepository {
    private val api = RetrofitClient.reportApi

    suspend fun createReport(request: CreateReportRequest) = api.createReport(request)
    suspend fun getReportById(id: Long) = api.getReportById(id)
    suspend fun getAllReports() = api.getAllReports()
    suspend fun getMyReports() = api.getMyReports()
    suspend fun getReportsByMunicipality(municipality: String) = api.getReportsByMunicipality(municipality)
}