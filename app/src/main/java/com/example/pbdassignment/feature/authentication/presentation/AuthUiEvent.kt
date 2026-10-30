package com.example.pbdassignment.feature.authentication.presentation

sealed class AuthUiEvent {
    data class OnEmailChanged(val email: String) : AuthUiEvent()
    data class OnPasswordChanged(val password: String) : AuthUiEvent()
    data class OnDisplayNameChanged(val name: String) : AuthUiEvent()
    object OnLoginSubmit : AuthUiEvent()
    object OnRegisterSubmit : AuthUiEvent()
    object OnClearError : AuthUiEvent()
}