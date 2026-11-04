package com.example.pbdassignment.core.model

import java.util.Calendar

/**
 * Encapsulates a time period filter for dashboard and analytics views.
 */
data class DatePeriod(
    val label: String,
    val startEpochMs: Long,
    val endEpochMs: Long
) {
    companion object {
        fun allTime(): DatePeriod = DatePeriod(
            label = "All Time",
            startEpochMs = 0L,
            endEpochMs = Long.MAX_VALUE
        )

        fun thisMonth(): DatePeriod {
            val cal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val start = cal.timeInMillis
            cal.add(Calendar.MONTH, 1)
            val end = cal.timeInMillis - 1
            return DatePeriod("This Month", start, end)
        }

        fun lastMonth(): DatePeriod {
            val cal = Calendar.getInstance().apply {
                add(Calendar.MONTH, -1)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val start = cal.timeInMillis
            cal.add(Calendar.MONTH, 1)
            val end = cal.timeInMillis - 1
            return DatePeriod("Last Month", start, end)
        }

        fun thisYear(): DatePeriod {
            val cal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val start = cal.timeInMillis
            cal.add(Calendar.YEAR, 1)
            val end = cal.timeInMillis - 1
            return DatePeriod("This Year", start, end)
        }

        val defaultPeriods = listOf(allTime(), thisMonth(), lastMonth(), thisYear())
    }
}

/**
 * Represents grouped income aggregated by stream source (Salary, Freelance, AdSense, Crypto, etc.).
 */
data class IncomeBySource(
    val sourceType: String,
    val totalLkr: Double,
    val percentageOfTotal: Double
)

/**
 * Represents grouped expenses aggregated by category (Housing, Food, Transport, etc.).
 */
data class ExpenseByCategory(
    val categoryName: String,
    val totalLkr: Double,
    val percentageOfTotal: Double
)

/**
 * High-level summary of active goal progress for the dashboard overview.
 */
data class GoalSummary(
    val goalId: String,
    val title: String,
    val targetAmountLkr: Double,
    val currentSavedLkr: Double,
    val progressFraction: Float,
    val remainingAmountLkr: Double,
    val requiredMonthlyContributionLkr: Double,
    val isCompleted: Boolean,
    val isOverdue: Boolean
)

/**
 * FinancialSnapshot represents the complete aggregated state of Kavindu's finances
 * for a selected reporting period.
 *
 * This contract isolates the UI/ViewModel from raw database operations.
 */
data class FinancialSnapshot(
    val period: DatePeriod = DatePeriod.allTime(),
    val totalIncomeLkr: Double = 0.0,
    val totalExpenseLkr: Double = 0.0,
    val netCashFlowLkr: Double = 0.0,
    val savingsRate: Double = 0.0,
    val committedExpenseLkr: Double = 0.0,
    val discretionaryExpenseLkr: Double = 0.0,
    val safeToSpendDailyLkr: Double = 0.0,
    val incomeBySource: List<IncomeBySource> = emptyList(),
    val expenseByCategory: List<ExpenseByCategory> = emptyList(),
    val activeGoalSummary: GoalSummary? = null,
    val isOffline: Boolean = false,
    val lastUpdatedEpochMs: Long = System.currentTimeMillis()
) {
    val isDeficit: Boolean
        get() = netCashFlowLkr < 0.0

    val committedRatio: Float
        get() = if (totalExpenseLkr > 0) (committedExpenseLkr / totalExpenseLkr).toFloat().coerceIn(0f, 1f) else 0f

    val discretionaryRatio: Float
        get() = if (totalExpenseLkr > 0) (discretionaryExpenseLkr / totalExpenseLkr).toFloat().coerceIn(0f, 1f) else 0f
}
