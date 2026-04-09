package mx.edu.utez.fmc_mobile.data.repository

import mx.edu.utez.fmc_mobile.data.remote.RetrofitClient
import mx.edu.utez.fmc_mobile.data.remote.dto.request.LeaderApplicationRequest
import mx.edu.utez.fmc_mobile.utils.SessionManager

class ApplicationRepository {
    private val api = RetrofitClient.applicationApi

    suspend fun applyAsVolunteer() = api.applyAsVolunteer()
    suspend fun applyAsLeader() = api.applyAsLeader(LeaderApplicationRequest(SessionManager.getUsername()))
    suspend fun getAllApplications() = api.getAllApplications()
    suspend fun getPendingApplications() = api.getPendingApplications()
    suspend fun getMyApplicationStatus() = api.getMyApplicationStatus()
}
