package com.example.pbd.data.repository

import com.example.pbd.core.data.FinanceRepository
import com.example.pbd.core.model.DatePeriod
import com.example.pbd.core.model.FinancialSnapshot
import com.example.pbd.core.model.SavingsGoal
import com.example.pbd.domain.dashboard.CalculateFinancialSnapshot
import com.example.pbd.domain.repository.DashboardRepository
import com.example.pbd.feature_goals.GoalRepositoryProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

/**
 * Production implementation of [DashboardRepository] that dynamically combines
 * real-time income, expense, and savings goal streams into a unified [FinancialSnapshot].
 */
class DashboardRepositoryImpl(
    private val calculateFinancialSnapshot: CalculateFinancialSnapshot = CalculateFinancialSnapshot()
) : DashboardRepository {

    override fun observeFinancialSnapshot(period: DatePeriod): Flow<FinancialSnapshot> {
        return combine(
            FinanceRepository.incomes,
            FinanceRepository.expenses,
            GoalRepositoryProvider.instance.observeGoals()
        ) { incomes, expenses, goals ->
            val activeGoal = goals.firstOrNull() ?: SavingsGoal.createDefaultMacBookGoal()
            calculateFinancialSnapshot(
                incomes = incomes,
                expenses = expenses,
                activeGoal = activeGoal,
                period = period
            )
        }
    }

    override suspend fun getFinancialSnapshot(period: DatePeriod): FinancialSnapshot {
        return observeFinancialSnapshot(period).first()
    }
}
