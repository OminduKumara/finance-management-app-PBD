package com.example.pbd.core.model

data class FinancialBaseline(
    val baseSalary: Double = 135000.0,
    val committedRent: Double = 34000.0,
    val committedGym: Double = 4500.0,
    val discretionaryBudgetCap: Double = 40000.0,
    val targetSavingsRate: Double = 39900.0,
    val rules: List<String> = listOf(
        "Auto-sweep 50% of freelance milestones to goal",
        "Keep daily dining and coffee under LKR 1,500",
        "Never allocate volatile crypto gains to lifestyle spending"
    )
) {
    val totalCommitted: Double
        get() = committedRent + committedGym
}
