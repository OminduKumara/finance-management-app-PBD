package com.example.pbdassignment.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Clarity", Icons.Default.Dashboard)
    object Income : Screen("income", "Inflows", Icons.Default.AccountBalanceWallet)
    object Expenses : Screen("expenses", "Outflows", Icons.Default.Receipt)
    object Goals : Screen("goals", "Goals", Icons.Default.Star)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

object GoalRoutes {
    const val LIST = "goals"
    const val DETAIL = "goal_detail/{goalId}"
    const val CREATE = "create_goal"

    fun detail(goalId: String) = "goal_detail/$goalId"
}

object AnalyticsRoutes {
    const val ANALYTICS = "analytics"
}

