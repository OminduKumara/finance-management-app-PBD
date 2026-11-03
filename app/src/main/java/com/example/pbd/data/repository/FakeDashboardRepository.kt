package com.example.pbd.data.repository

import com.example.pbd.core.model.DatePeriod
import com.example.pbd.core.model.ExpenseByCategory
import com.example.pbd.core.model.FinancialSnapshot
import com.example.pbd.core.model.GoalSummary
import com.example.pbd.core.model.IncomeBySource
import com.example.pbd.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Deterministic in-memory fake implementation of [DashboardRepository].
 *
 * Enables completely independent development, UI testing, and Compose previews
 * without any runtime dependency on Firestore or Members 1 and 2 implementations.
 */
class FakeDashboardRepository(
    initialSnapshot: FinancialSnapshot = createDefaultKavinduSnapshot()
) : DashboardRepository {

    private val _snapshotFlow = MutableStateFlow(initialSnapshot)

    override fun observeFinancialSnapshot(period: DatePeriod): Flow<FinancialSnapshot> {
        return _snapshotFlow.asStateFlow()
    }

    override suspend fun getFinancialSnapshot(period: DatePeriod): FinancialSnapshot {
        return _snapshotFlow.value
    }

    /**
     * Mutates the current snapshot for UI testing of various state transitions.
     */
    fun emitSnapshot(snapshot: FinancialSnapshot) {
        _snapshotFlow.value = snapshot
    }

    /**
     * Emits an empty financial scenario (e.g. newly onboarded user with 0 records).
     */
    fun emitEmptyScenario(period: DatePeriod = DatePeriod.allTime()) {
        _snapshotFlow.value = FinancialSnapshot(
            period = period,
            totalIncomeLkr = 0.0,
            totalExpenseLkr = 0.0,
            netCashFlowLkr = 0.0,
            savingsRate = 0.0,
            committedExpenseLkr = 0.0,
            discretionaryExpenseLkr = 0.0,
            safeToSpendDailyLkr = 0.0,
            incomeBySource = emptyList(),
            expenseByCategory = emptyList(),
            activeGoalSummary = null,
            isOffline = false
        )
    }

    /**
     * Emits a financial deficit scenario where expenses exceed income.
     */
    fun emitDeficitScenario() {
        _snapshotFlow.value = FinancialSnapshot(
            period = DatePeriod.allTime(),
            totalIncomeLkr = 120000.0,
            totalExpenseLkr = 145000.0,
            netCashFlowLkr = -25000.0,
            savingsRate = -0.2083,
            committedExpenseLkr = 85000.0,
            discretionaryExpenseLkr = 60000.0,
            safeToSpendDailyLkr = 0.0,
            incomeBySource = listOf(
                IncomeBySource("Salary", 120000.0, 100.0)
            ),
            expenseByCategory = listOf(
                ExpenseByCategory("Rent & Housing", 34000.0, 23.45),
                ExpenseByCategory("Transport", 30000.0, 20.69),
                ExpenseByCategory("Food & Dining", 45000.0, 31.03)
            ),
            activeGoalSummary = null,
            isOffline = false
        )
    }

    /**
     * Simulates toggling offline / cached data mode.
     */
    fun setOffline(isOffline: Boolean) {
        _snapshotFlow.update { it.copy(isOffline = isOffline) }
    }

    companion object {
        /**
         * Authentic baseline scenario for Kavindu Silva based on problem specification.
         */
        fun createDefaultKavinduSnapshot(): FinancialSnapshot {
            return FinancialSnapshot(
                period = DatePeriod.allTime(),
                totalIncomeLkr = 180000.0,
                totalExpenseLkr = 76450.0,
                netCashFlowLkr = 103550.0,
                savingsRate = 0.5753,
                committedExpenseLkr = 38500.0, // Rent 34k + Gym 4.5k
                discretionaryExpenseLkr = 37950.0, // PickMe, UberEats, Dining
                safeToSpendDailyLkr = 3386.67,
                incomeBySource = listOf(
                    IncomeBySource("Salary", 135000.0, 75.0),
                    IncomeBySource("Freelance", 45000.0, 25.0)
                ),
                expenseByCategory = listOf(
                    ExpenseByCategory("Rent & Housing", 34000.0, 44.47),
                    ExpenseByCategory("Food & Dining", 24000.0, 31.39),
                    ExpenseByCategory("Transport (PickMe)", 13950.0, 18.25),
                    ExpenseByCategory("Gym & Fitness", 4500.0, 5.89)
                ),
                activeGoalSummary = GoalSummary(
                    goalId = "macbook_pro_m4",
                    title = "MacBook Pro M4 (14-inch)",
                    targetAmountLkr = 490000.0,
                    currentSavedLkr = 11200.0,
                    progressFraction = 0.0229f,
                    remainingAmountLkr = 478800.0,
                    requiredMonthlyContributionLkr = 39900.0,
                    isCompleted = false,
                    isOverdue = false
                ),
                isOffline = false
            )
        }
    }
}
