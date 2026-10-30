package com.example.pbdassignment.feature.authentication.presentation

data class AuthUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val displayName: String = "",
    val displayNameError: String? = null,
    val isLoading: Boolean = false,
    val authSuccess: Boolean = false,
    val generalErrorMessage: String? = null
)