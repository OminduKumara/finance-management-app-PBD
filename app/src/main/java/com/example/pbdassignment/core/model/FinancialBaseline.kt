package com.example.pbdassignment.core.model

data class FinancialBaseline(
    val baseSalary: Double = 0.0,
    val committedRent: Double = 0.0,
    val committedGym: Double = 0.0,
    val discretionaryBudgetCap: Double = 0.0,
    val targetSavingsRate: Double = 0.0,
    val rules: List<String> = emptyList()
) {
    val totalCommitted: Double
        get() = committedRent + committedGym
}
