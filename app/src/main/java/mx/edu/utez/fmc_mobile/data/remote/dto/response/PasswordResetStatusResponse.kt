package mx.edu.utez.fmc_mobile.data.remote.dto.response

data class PasswordResetStatusResponse(
    val stage: String,
    val canRequestNewCode: Boolean,
    val cooldownUntil: String?,
    val codeExpiresAt: String?,
    val remainingVerifyAttempts: Int?
)

