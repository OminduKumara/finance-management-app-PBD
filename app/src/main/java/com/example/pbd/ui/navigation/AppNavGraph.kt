package com.example.pbd.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pbd.core.firebase.FirebaseManager
import com.example.pbd.feature_auth.AuthScreen
import com.example.pbd.feature_dashboard.DashboardScreen
import com.example.pbd.feature_expense.ExpenseScreen
import com.example.pbd.feature_income.IncomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val currentUser by FirebaseManager.currentUser.collectAsState()

    if (currentUser == null) {
        AuthScreen(onAuthSuccess = {})
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigationItems = listOf(
        Screen.Dashboard,
        Screen.Income,
        Screen.Expenses,
        Screen.Goals,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                navigationItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToAnalytics = { navController.navigate(AnalyticsRoutes.ANALYTICS) }
                )
            }
            composable(Screen.Income.route) {
                IncomeScreen()
            }
            composable(Screen.Expenses.route) {
                ExpenseScreen()
            }
            composable(Screen.Goals.route) {
                com.example.pbd.feature_goals.GoalListScreen(
                    onGoalClick = { goalId -> navController.navigate(GoalRoutes.detail(goalId)) },
                    onCreateGoalClick = { navController.navigate(GoalRoutes.CREATE) }
                )
            }
            composable(GoalRoutes.DETAIL) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getString("goalId") ?: "macbook_pro_m4"
                com.example.pbd.feature_goals.GoalDetailScreen(
                    goalId = goalId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(GoalRoutes.CREATE) {
                com.example.pbd.feature_goals.CreateGoalScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(AnalyticsRoutes.ANALYTICS) {
                com.example.pbd.feature_analytics.AnalyticsScreen()
            }
            composable(Screen.Profile.route) {
                com.example.pbd.feature_profile.ProfileScreen()
            }
        }
    }
}
