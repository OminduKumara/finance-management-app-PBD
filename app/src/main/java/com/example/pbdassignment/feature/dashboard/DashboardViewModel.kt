package com.example.pbdassignment.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.core.data.FinanceRepository
import com.example.pbdassignment.core.model.ExpenseNature
import com.example.pbdassignment.core.model.GoalItem
import com.example.pbdassignment.core.model.InvoiceStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class DashboardUiState(
    val realizedIncomeLkr: Double = 0.0,
    val totalExpenseLkr: Double = 0.0,
    val netCashflowLkr: Double = 0.0,
    val committedExpenseLkr: Double = 0.0,
    val discretionaryExpenseLkr: Double = 0.0,
    val lifestylePoolLkr: Double = 0.0,
    val safeToSpendDailyLkr: Double = 0.0,
    val daysRemainingInMonth: Int = 26,
    val savingsRatePercent: Double = 0.0,
    val goal: GoalItem = GoalItem(),
    val projectedMonthsToGoal: Double = 0.0,
    val committedRatio: Float = 0f,
    val discretionaryRatio: Float = 0f,
    val savingsRatio: Float = 0f
)

class DashboardViewModel : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        FinanceRepository.incomes,
        FinanceRepository.expenses,
        FinanceRepository.goal
    ) { incomes, expenses, goal ->
        val realizedIncome = incomes
            .filter { it.invoiceStatus == InvoiceStatus.RECEIVED || it.invoiceStatus == InvoiceStatus.SETTLED }
            .sumOf { it.lkrAmount }

        val totalExpense = expenses.sumOf { it.amount }
        val netCashflow = realizedIncome - totalExpense
        val committed = expenses.filter { it.nature == ExpenseNature.COMMITTED }.sumOf { it.amount }
        val discretionary = expenses.filter { it.nature == ExpenseNature.DISCRETIONARY }.sumOf { it.amount }

        // Calendar days remaining calculation
        val calendar = Calendar.getInstance()
        val totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val daysRemaining = (totalDays - currentDay + 1).coerceAtLeast(1)

        // Daily Safe-To-Spend Allowance: (Income - Committed Costs - Required Monthly MacBook Savings) / Days Remaining
        val remainingForLifestyle = (realizedIncome - committed - goal.requiredMonthlySavings)
            .coerceAtLeast(0.0)
        val dailySafeToSpend = remainingForLifestyle / daysRemaining.toDouble()

        // Savings retention rate
        val savingsRate = if (realizedIncome > 0) ((netCashflow / realizedIncome) * 100.0).coerceIn(0.0, 100.0) else 0.0

        // Runway calculation based on current net monthly savings
        val monthlySavingsVelocity = (realizedIncome - totalExpense).coerceAtLeast(1000.0)
        val projectedMonths = if (goal.remainingDeficit <= 0.0) 0.0 else (goal.remainingDeficit / monthlySavingsVelocity).coerceIn(1.0, 60.0)

        // Ratios
        val totalOutflowAndSavings = committed + discretionary + goal.requiredMonthlySavings
        val committedFraction = if (totalOutflowAndSavings > 0.0) (committed / totalOutflowAndSavings).toFloat().coerceIn(0f, 1f) else 0f
        val discretionaryFraction = if (totalOutflowAndSavings > 0.0) (discretionary / totalOutflowAndSavings).toFloat().coerceIn(0f, 1f) else 0f
        val savingsFraction = if (totalOutflowAndSavings > 0.0) (goal.requiredMonthlySavings / totalOutflowAndSavings).toFloat().coerceIn(0f, 1f) else 0f

        DashboardUiState(
            realizedIncomeLkr = realizedIncome,
            totalExpenseLkr = totalExpense,
            netCashflowLkr = netCashflow,
            committedExpenseLkr = committed,
            discretionaryExpenseLkr = discretionary,
            lifestylePoolLkr = remainingForLifestyle,
            safeToSpendDailyLkr = dailySafeToSpend,
            daysRemainingInMonth = daysRemaining,
            savingsRatePercent = savingsRate,
            goal = goal,
            projectedMonthsToGoal = projectedMonths,
            committedRatio = committedFraction,
            discretionaryRatio = discretionaryFraction,
            savingsRatio = savingsFraction
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun allocateToGoal(amount: Double) {
        FinanceRepository.allocateToGoal(amount)
    }

    fun updateGoal(title: String, targetAmount: Double, currentSaved: Double, months: Int) {
        FinanceRepository.updateFullGoal(title, targetAmount, currentSaved, months)
    }
}
