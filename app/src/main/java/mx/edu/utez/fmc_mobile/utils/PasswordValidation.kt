package mx.edu.utez.fmc_mobile.utils

data class PasswordValidationResult(
    val hasMinLength: Boolean,
    val hasLowerAndUpper: Boolean,
    val hasSpecialChar: Boolean
) {
    val isValid: Boolean
        get() = hasMinLength && hasLowerAndUpper && hasSpecialChar
}

object PasswordValidator {
    private val lowerCaseRegex = ".*[a-z].*".toRegex()
    private val upperCaseRegex = ".*[A-Z].*".toRegex()
    private val specialCharRegex = ".*[!@#$%^&*(),.?\":{}|<>].*".toRegex()

    fun evaluate(password: String): PasswordValidationResult {
        return PasswordValidationResult(
            hasMinLength = password.length >= 8,
            hasLowerAndUpper = password.contains(lowerCaseRegex) && password.contains(upperCaseRegex),
            hasSpecialChar = password.contains(specialCharRegex)
        )
    }
}

