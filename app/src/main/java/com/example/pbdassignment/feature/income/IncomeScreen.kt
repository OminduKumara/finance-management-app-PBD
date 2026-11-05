package com.example.pbdassignment.feature.income

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.pbdassignment.feature.income.presentation.IncomeViewModel
import com.example.pbdassignment.feature.income.presentation.IncomeViewModelFactory

private object IncomeNav {
    const val DASHBOARD = "income_dashboard"
    const val ADD_INCOME = "income_add"
    const val CRYPTO_PNL = "income_crypto_pnl"
}
@Composable
fun IncomeScreen(
    viewModel: IncomeViewModel = viewModel(factory = IncomeViewModelFactory())
) {
    val state by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = IncomeNav.DASHBOARD) {
        composable(IncomeNav.DASHBOARD) {
            IncomeDashboardScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToAddIncome = { navController.navigate(IncomeNav.ADD_INCOME) },
                onNavigateToCryptoPnL = { navController.navigate(IncomeNav.CRYPTO_PNL) }
            )
        }
        composable(IncomeNav.ADD_INCOME) {
            AddIncomeScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(IncomeNav.CRYPTO_PNL) {
            CryptoPnLScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}