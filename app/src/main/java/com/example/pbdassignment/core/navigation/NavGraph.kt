package com.example.pbdassignment.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pbdassignment.feature.authentication.data.AuthRepositoryImpl
import com.example.pbdassignment.feature.authentication.domain.Login
import com.example.pbdassignment.feature.authentication.domain.Register
import com.example.pbdassignment.feature.authentication.presentation.AuthViewModel
import com.example.pbdassignment.feature.authentication.presentation.LoginScreen
import com.example.pbdassignment.feature.authentication.presentation.RegisterScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    // Single shared repository & use-cases
    val authRepository = rememberAuthRepository()
    val loginUseCase = Login(authRepository)
    val registerUseCase = Register(authRepository)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = viewModel {
                AuthViewModel(loginUseCase, registerUseCase)
            }
            val state by viewModel.uiState.collectAsState()

            LoginScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.IncomeDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            val viewModel: AuthViewModel = viewModel {
                AuthViewModel(loginUseCase, registerUseCase)
            }
            val state by viewModel.uiState.collectAsState()

            RegisterScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.IncomeDashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Placeholder for Income Dashboard (Built on Day 2 & Day 3)
        composable(Screen.IncomeDashboard.route) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Income Dashboard")
            }
        }
    }
}

@Composable
private fun rememberAuthRepository(): AuthRepositoryImpl {
    return androidx.compose.runtime.remember { AuthRepositoryImpl() }
}