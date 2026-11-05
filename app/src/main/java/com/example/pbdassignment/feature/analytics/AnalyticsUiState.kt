package com.example.pbdassignment.feature.analytics

import com.example.pbdassignment.core.model.DatePeriod
import com.example.pbdassignment.core.model.FinancialSnapshot

/**
 * Immutable UI state for the Financial Analytics and Visualisations screen.
 * Simple, declarative, and directly driven by the repository stream.
 */
data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val selectedPeriod: DatePeriod = DatePeriod.allTime(),
    val availablePeriods: List<DatePeriod> = DatePeriod.defaultPeriods,
    val snapshot: FinancialSnapshot = FinancialSnapshot(),
    val errorMessage: String? = null
) {
    val isExpenseEmpty: Boolean
        get() = snapshot.expenseByCategory.isEmpty()

    val isIncomeEmpty: Boolean
        get() = snapshot.incomeBySource.isEmpty()
}
