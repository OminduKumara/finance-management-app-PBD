package com.example.pbdassignment.feature.authentication.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp


@Composable
fun LoginScreen(
    state: AuthUiState,
    onEvent: (AuthUiEvent) -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(state.authSuccess) {
        if (state.authSuccess) {
            onLoginSuccess()
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Manage your income & savings with clarity",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )
            // Email Field
            OutlinedTextField(
                value = state.email,
                onValueChange = { onEvent(AuthUiEvent.OnEmailChanged(it)) },
                label = { Text("Email Address") },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Password Field
            OutlinedTextField(
                value = state.password,
                onValueChange = { onEvent(AuthUiEvent.OnPasswordChanged(it)) },
                label = { Text("Password") },
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onEvent(AuthUiEvent.OnLoginSubmit)
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            state.generalErrorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Submit Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onEvent(AuthUiEvent.OnLoginSubmit)
                },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign In", fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Switch to Register
            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Sign up")
            }
        }
    }
}