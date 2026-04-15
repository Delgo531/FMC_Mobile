package mx.edu.utez.fmc_mobile.data.repository

import mx.edu.utez.fmc_mobile.data.remote.RetrofitClient
import mx.edu.utez.fmc_mobile.data.remote.dto.request.ForgotPasswordRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.LoginRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.RegisterRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.ResetPasswordRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.VerifyResetCodeRequest

class AuthRepository {

    private val api = RetrofitClient.authApi

    suspend fun register(request: RegisterRequest) = api.register(request)

    suspend fun login(request: LoginRequest) = api.login(request)

    suspend fun logout() = api.logout()

    suspend fun forgotPassword(request: ForgotPasswordRequest) = api.forgotPassword(request)

    suspend fun getPasswordResetStatus(email: String) = api.getPasswordResetStatus(email)

    suspend fun verifyResetCode(request: VerifyResetCodeRequest) = api.verifyResetCode(request)

    suspend fun resetPassword(request: ResetPasswordRequest) = api.resetPassword(request)
}