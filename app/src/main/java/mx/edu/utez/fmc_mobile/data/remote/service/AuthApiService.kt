package mx.edu.utez.fmc_mobile.data.remote.service

import mx.edu.utez.fmc_mobile.data.remote.dto.request.ForgotPasswordRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.LoginRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.RegisterRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.ResetPasswordRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.VerifyResetCodeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Map<String, Any>>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<Map<String, Any>>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Map<String, Any>>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Map<String, Any>>

    @POST("api/auth/verify-reset-code")
    suspend fun verifyResetCode(@Body request: VerifyResetCodeRequest): Response<Map<String, Any>>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Map<String, Any>>
}