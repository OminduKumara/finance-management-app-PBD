package com.example.pbdassignment.feature.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.core.common.Resource
import com.example.pbdassignment.core.navigation.Screen
import com.example.pbdassignment.feature.authentication.domain.Login
import com.example.pbdassignment.feature.authentication.domain.Register
import com.example.pbdassignment.feature.authentication.domain.ValidateEmail
import com.example.pbdassignment.feature.authentication.domain.ValidatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val login: Login,
    private val register: Register,
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validatePassword: ValidatePassword = ValidatePassword()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email, emailError = null) }
            }
            is AuthUiEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password, passwordError = null) }
            }
            is AuthUiEvent.OnDisplayNameChanged -> {
                _uiState.update { it.copy(displayName = event.name, displayNameError = null) }
            }
            is AuthUiEvent.OnLoginSubmit -> login()
            is AuthUiEvent.OnRegisterSubmit -> register()
            is AuthUiEvent.OnClearError -> {
                _uiState.update { it.copy(generalErrorMessage = null) }
            }
        }
    }
    private fun login() {
        val emailResult = validateEmail(_uiState.value.email)
        val passwordResult = validatePassword(_uiState.value.password)
        if (!emailResult.successful || !passwordResult.successful) {
            _uiState.update {
                it.copy(
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage
                )
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalErrorMessage = null) }
            when (val result = login(_uiState.value.email, _uiState.value.password)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, authSuccess = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, generalErrorMessage = result.message) }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    private fun register() {
        val emailResult = validateEmail(_uiState.value.email)
        val passwordResult = validatePassword(_uiState.value.password)
        val nameBlank = _uiState.value.displayName.isBlank()
        if (!emailResult.successful || !passwordResult.successful || nameBlank) {
            _uiState.update {
                it.copy(
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                    displayNameError = if (nameBlank) "Name cannot be empty" else null
                )
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalErrorMessage = null) }
            val result = register(
                email = _uiState.value.email,
                password = _uiState.value.password,
                displayName = _uiState.value.displayName
            )
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, authSuccess = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, generalErrorMessage = result.message) }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}