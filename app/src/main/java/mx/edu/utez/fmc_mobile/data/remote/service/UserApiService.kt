package mx.edu.utez.fmc_mobile.data.remote.service

import mx.edu.utez.fmc_mobile.data.remote.dto.request.AdminRegisterRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.DeactivateAccountRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateAdminPermissionsRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {

    @GET("api/users")
    suspend fun getAllUsers(): Response<Map<String, Any>>

    @GET("api/users/volunteers")
    suspend fun getVolunteers(): Response<Map<String, Any>>

    @POST("api/users")
    suspend fun registerAdmin(@Body request: AdminRegisterRequest): Response<Map<String, Any>>

    @PUT("api/users/permissions/{userId}")
    suspend fun updateAdminPermissions(
        @Path("userId") userId: Long,
        @Body request: UpdateAdminPermissionsRequest
    ): Response<Map<String, Any>>

    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body request: UpdateUserRequest
    ): Response<Map<String, Any>>

    @PATCH("api/users/{id}")
    suspend fun disableUser(@Path("id") id: Long): Response<Map<String, Any>>

    @PATCH("api/users/disableMyAccount")
    suspend fun deactivateOwnAccount(@Body request: DeactivateAccountRequest): Response<Map<String, Any>>

    @GET("api/users/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): Response<Map<String, Any>>

    @GET("api/users/by-id/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<Map<String, Any>>
}