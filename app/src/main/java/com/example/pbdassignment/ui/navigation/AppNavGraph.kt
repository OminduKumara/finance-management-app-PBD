package com.example.pbdassignment.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pbdassignment.feature.analytics.AnalyticsScreen
import com.example.pbdassignment.feature.dashboard.DashboardScreen
import com.example.pbdassignment.feature.expense.presentation.ExpenseScreen
import com.example.pbdassignment.feature.goals.CreateGoalScreen
import com.example.pbdassignment.feature.goals.GoalDetailScreen
import com.example.pbdassignment.feature.goals.GoalListScreen
import com.example.pbdassignment.feature.income.IncomeScreen
import com.example.pbdassignment.feature.profile.ProfileScreen
import com.example.pbdassignment.ui.theme.BrandPrimary

@Composable
fun MainAppScreen(
    onSignOut: () -> Unit = {}
) {
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

    val isTopLevelRoute = currentRoute in navigationItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (isTopLevelRoute) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    navigationItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title, style = MaterialTheme.typography.labelSmall) },
                            selected = currentRoute == screen.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandPrimary,
                                selectedTextColor = BrandPrimary,
                                indicatorColor = BrandPrimary.copy(alpha = 0.12f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToAnalytics = { navController.navigate(AnalyticsRoutes.ANALYTICS) },
                    onNavigateToIncome = { navController.navigate(Screen.Income.route) },
                    onNavigateToExpenses = { navController.navigate(Screen.Expenses.route) },
                    onNavigateToGoals = { navController.navigate(Screen.Goals.route) }
                )
            }
            composable(Screen.Income.route) {
                IncomeScreen()
            }
            composable(Screen.Expenses.route) {
                ExpenseScreen()
            }
            composable(Screen.Goals.route) {
                GoalListScreen(
                    onGoalClick = { goalId -> navController.navigate(GoalRoutes.detail(goalId)) }
                )
            }
            composable(GoalRoutes.DETAIL) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getString("goalId") ?: "macbook_pro_m4"
                GoalDetailScreen(
                    goalId = goalId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(GoalRoutes.CREATE) {
                CreateGoalScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(AnalyticsRoutes.ANALYTICS) {
                AnalyticsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(onSignOut = onSignOut)
            }
        }
    }
}
