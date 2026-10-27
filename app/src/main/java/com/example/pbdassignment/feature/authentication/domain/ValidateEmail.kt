package com.example.pbdassignment.feature.authentication.domain

class ValidateEmail {
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(successful = false, errorMessage = "Email cannot be empty")
        }
        if (!email.matches(emailRegex)) {
            return ValidationResult(successful = false, errorMessage = "Invalid email format")
        }
        return ValidationResult(successful = true)
    }
}
data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)