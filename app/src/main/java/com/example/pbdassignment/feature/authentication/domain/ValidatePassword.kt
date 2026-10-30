package com.example.pbdassignment.feature.authentication.domain

class ValidatePassword {
    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(successful = false, errorMessage = "Password cannot be empty")
        }
        if (password.length < 6) {
            return ValidationResult(successful = false, errorMessage = "Password must be at least 6 characters")
        }
        return ValidationResult(successful = true)
    }
}