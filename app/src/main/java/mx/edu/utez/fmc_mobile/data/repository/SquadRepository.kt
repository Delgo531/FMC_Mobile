package mx.edu.utez.fmc_mobile.data.repository

import mx.edu.utez.fmc_mobile.data.remote.RetrofitClient
import mx.edu.utez.fmc_mobile.data.remote.dto.request.CreateSquadRequest

class SquadRepository {
    private val api = RetrofitClient.squadApi

    suspend fun createSquad(request: CreateSquadRequest) = api.createSquad(request)

    suspend fun getAssignedReports() = api.getAssignedReports()
}