package mx.edu.utez.fmc_mobile.data.repository

import mx.edu.utez.fmc_mobile.data.remote.RetrofitClient

class ApplicationRepository {
    private val api = RetrofitClient.applicationApi

    suspend fun applyAsVolunteer() = api.applyAsVolunteer()
    suspend fun applyAsLeader() = api.applyAsLeader()
    suspend fun getAllApplications() = api.getAllApplications()
    suspend fun getPendingApplications() = api.getPendingApplications()
    suspend fun getMyApplicationStatus() = api.getMyApplicationStatus()
}
