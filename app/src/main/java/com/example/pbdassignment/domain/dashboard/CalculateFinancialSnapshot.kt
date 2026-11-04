package com.example.pbdassignment.domain.dashboard

import com.example.pbdassignment.core.model.DatePeriod
import com.example.pbdassignment.core.model.ExpenseByCategory
import com.example.pbdassignment.core.model.ExpenseItem
import com.example.pbdassignment.core.model.ExpenseNature
import com.example.pbdassignment.core.model.FinancialSnapshot
import com.example.pbdassignment.core.model.GoalSummary
import com.example.pbdassignment.core.model.IncomeBySource
import com.example.pbdassignment.core.model.IncomeItem
import com.example.pbdassignment.core.model.InvoiceStatus
import com.example.pbdassignment.core.model.SavingsGoal
import com.example.pbdassignment.domain.goals.CalculateGoalProgress
import kotlin.math.round

/**
 * Pure Kotlin business logic interactor for aggregating financial records
 * into a single unified FinancialSnapshot.
 *
 * Implements strict rules for zero-division avoidance, multi-stream groupings,
 * committed vs. discretionary segmentation, and daily safe-to-spend calculation.
 */
class CalculateFinancialSnapshot(
    private val calculateGoalProgress: CalculateGoalProgress = CalculateGoalProgress()
) {

    operator fun invoke(
        incomes: List<IncomeItem>,
        expenses: List<ExpenseItem>,
        activeGoal: SavingsGoal? = null,
        period: DatePeriod = DatePeriod.allTime(),
        daysRemainingInCycle: Int = 30,
        currentEpochMs: Long = System.currentTimeMillis()
    ): FinancialSnapshot {

        // 1. Filter by period boundaries
        val filteredIncomes = incomes.filter { income ->
            income.date in period.startEpochMs..period.endEpochMs &&
                    (income.invoiceStatus == InvoiceStatus.RECEIVED || income.invoiceStatus == InvoiceStatus.SETTLED)
        }

        val filteredExpenses = expenses.filter { expense ->
            expense.timestamp in period.startEpochMs..period.endEpochMs
        }

        // 2. Inflows & Outflows
        val totalIncome = roundToTwoDecimals(filteredIncomes.sumOf { it.lkrAmount })
        val totalExpense = roundToTwoDecimals(filteredExpenses.sumOf { it.amount })
        val netCashFlow = roundToTwoDecimals(totalIncome - totalExpense)

        // 3. Savings Rate (% of realized income retained)
        val savingsRate = if (totalIncome > 0.0) {
            roundToFourDecimals(netCashFlow / totalIncome)
        } else {
            0.0
        }

        // 4. Committed vs. Discretionary Segmentation
        val committedExpense = roundToTwoDecimals(
            filteredExpenses
                .filter { it.nature == ExpenseNature.COMMITTED }
                .sumOf { it.amount }
        )

        val discretionaryExpense = roundToTwoDecimals(
            filteredExpenses
                .filter { it.nature == ExpenseNature.DISCRETIONARY }
                .sumOf { it.amount }
        )

        // 5. Income by Source Breakdown
        val incomeBySource = if (totalIncome > 0.0) {
            filteredIncomes
                .groupBy { it.streamType }
                .map { (source, items) ->
                    val sourceTotal = items.sumOf { it.lkrAmount }
                    val percentage = (sourceTotal / totalIncome) * 100.0
                    IncomeBySource(
                        sourceType = source,
                        totalLkr = roundToTwoDecimals(sourceTotal),
                        percentageOfTotal = roundToTwoDecimals(percentage)
                    )
                }
                .sortedByDescending { it.totalLkr }
        } else {
            emptyList()
        }

        // 6. Expense by Category Breakdown
        val expenseByCategory = if (totalExpense > 0.0) {
            filteredExpenses
                .groupBy { it.category }
                .map { (category, items) ->
                    val categoryTotal = items.sumOf { it.amount }
                    val percentage = (categoryTotal / totalExpense) * 100.0
                    ExpenseByCategory(
                        categoryName = category,
                        totalLkr = roundToTwoDecimals(categoryTotal),
                        percentageOfTotal = roundToTwoDecimals(percentage)
                    )
                }
                .sortedByDescending { it.totalLkr }
        } else {
            emptyList()
        }

        // 7. Active Goal Progress
        val goalSummary = activeGoal?.let { goal ->
            val progressResult = calculateGoalProgress(
                targetAmountLkr = goal.targetAmountLkr,
                currentContributionTotalLkr = goal.totalSavedLkr,
                deadlineEpochMs = goal.deadlineEpochMs,
                currentEpochMs = currentEpochMs
            )

            GoalSummary(
                goalId = goal.id,
                title = goal.name,
                targetAmountLkr = goal.targetAmountLkr,
                currentSavedLkr = goal.totalSavedLkr,
                progressFraction = progressResult.progressFraction,
                remainingAmountLkr = progressResult.remainingAmountLkr,
                requiredMonthlyContributionLkr = progressResult.requiredMonthlyContributionLkr,
                isCompleted = progressResult.isCompleted,
                isOverdue = progressResult.isOverdue
            )
        }

        // 8. Daily Safe-to-Spend Allowance
        // Formula: (Income - Committed Bills - Goal Required Contribution) / daysRemaining
        val requiredMonthlyGoal = goalSummary?.requiredMonthlyContributionLkr ?: 0.0
        val remainingForLifestyle = (totalIncome - committedExpense - requiredMonthlyGoal).coerceAtLeast(0.0)
        val validDays = daysRemainingInCycle.coerceAtLeast(1)
        val dailySafeToSpend = roundToTwoDecimals(remainingForLifestyle / validDays)

        return FinancialSnapshot(
            period = period,
            totalIncomeLkr = totalIncome,
            totalExpenseLkr = totalExpense,
            netCashFlowLkr = netCashFlow,
            savingsRate = savingsRate,
            committedExpenseLkr = committedExpense,
            discretionaryExpenseLkr = discretionaryExpense,
            safeToSpendDailyLkr = dailySafeToSpend,
            incomeBySource = incomeBySource,
            expenseByCategory = expenseByCategory,
            activeGoalSummary = goalSummary,
            isOffline = false,
            lastUpdatedEpochMs = currentEpochMs
        )
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100.0) / 100.0
    }

    private fun roundToFourDecimals(value: Double): Double {
        return round(value * 10000.0) / 10000.0
    }
}
