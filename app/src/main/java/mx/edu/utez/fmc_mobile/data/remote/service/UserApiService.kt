package mx.edu.utez.fmc_mobile.data.remote.service

import mx.edu.utez.fmc_mobile.data.remote.dto.request.AdminRegisterRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.DeactivateAccountRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateAdminPermissionsRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateUserRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.response.AdminUserResponse
import mx.edu.utez.fmc_mobile.data.remote.dto.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {

    @GET("api/users")
    suspend fun getAllUsers(): Response<List<Any>>

    @GET("api/users/volunteers")
    suspend fun getVolunteers(): Response<List<Any>>

    @POST("api/users")
    suspend fun registerAdmin(@Body request: AdminRegisterRequest): Response<AdminUserResponse>

    @PUT("api/users/permissions/{userId}")
    suspend fun updateAdminPermissions(
        @Path("userId") userId: Long,
        @Body request: UpdateAdminPermissionsRequest
    ): Response<Map<String, String>>

    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body request: UpdateUserRequest
    ): Response<UserResponse>

    @PATCH("api/users/{id}")
    suspend fun disableUser(@Path("id") id: Long): Response<AdminUserResponse>

    @PATCH("api/users/disableMyAccount")
    suspend fun deactivateOwnAccount(@Body request: DeactivateAccountRequest): Response<UserResponse>
}