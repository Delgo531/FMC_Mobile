package mx.edu.utez.fmc_mobile.data.remote.dto.request

data class ResetPasswordRequest(
    val resetToken: String,
    val newPassword: String,
    val confirmPassword: String
)