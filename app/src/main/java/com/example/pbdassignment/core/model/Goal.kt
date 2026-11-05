package com.example.pbdassignment.core.model

data class GoalItem(
    val id: String = "primary_goal",
    val title: String = "Savings Goal",
    val targetAmount: Double = 0.0,
    val currentSaved: Double = 0.0,
    val targetMonths: Int = 12
) {
    val remainingDeficit: Double
        get() = (targetAmount - currentSaved).coerceAtLeast(0.0)

    val progressFraction: Float
        get() = if (targetAmount > 0) (currentSaved / targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    val requiredMonthlySavings: Double
        get() = if (targetMonths > 0) remainingDeficit / targetMonths else 0.0

    val requiredDailySavings: Double
        get() = requiredMonthlySavings / 30.0
}
