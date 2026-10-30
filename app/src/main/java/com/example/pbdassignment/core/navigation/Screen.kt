package com.example.pbdassignment.core.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object IncomeDashboard : Screen("income_dashboard_screen")
}