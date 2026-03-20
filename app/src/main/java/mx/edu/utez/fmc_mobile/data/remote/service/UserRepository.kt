package mx.edu.utez.fmc_mobile.data.repository

import mx.edu.utez.fmc_mobile.data.remote.RetrofitClient
import mx.edu.utez.fmc_mobile.data.remote.dto.request.AdminRegisterRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.DeactivateAccountRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateAdminPermissionsRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateUserRequest

class UserRepository {
    private val api = RetrofitClient.userApi

    suspend fun getAllUsers() = api.getAllUsers()
    suspend fun getVolunteers() = api.getVolunteers()
    suspend fun registerAdmin(request: AdminRegisterRequest) = api.registerAdmin(request)
    suspend fun updateAdminPermissions(userId: Long, request: UpdateAdminPermissionsRequest) =
        api.updateAdminPermissions(userId, request)
    suspend fun updateUser(id: Long, request: UpdateUserRequest) = api.updateUser(id, request)
    suspend fun disableUser(id: Long) = api.disableUser(id)
    suspend fun deactivateOwnAccount(request: DeactivateAccountRequest) =
        api.deactivateOwnAccount(request)
    suspend fun getUserByUsername(username: String) = api.getUserByUsername(username)
    suspend fun getUserById(id: Long) = api.getUserById(id)
}