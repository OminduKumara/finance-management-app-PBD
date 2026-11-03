package com.example.pbd.domain.dashboard

import com.example.pbd.core.model.DatePeriod
import com.example.pbd.core.model.DefaultExpenseCategories
import com.example.pbd.core.model.ExpenseItem
import com.example.pbd.core.model.ExpenseNature
import com.example.pbd.core.model.IncomeItem
import com.example.pbd.core.model.IncomeStreams
import com.example.pbd.core.model.InvoiceStatus
import com.example.pbd.core.model.PaymentSource
import com.example.pbd.core.model.SavingsGoal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculateFinancialSnapshotTest {

    private lateinit var calculateSnapshot: CalculateFinancialSnapshot
    private val baseTimestamp = 1730190000000L // Fixed deterministic epoch

    @Before
    fun setup() {
        calculateSnapshot = CalculateFinancialSnapshot()
    }

    @Test
    fun `normal income and expenses produces accurate totals and cash flow`() {
        val incomes = listOf(
            IncomeItem(
                streamType = IncomeStreams.SALARY,
                title = "Engineering Salary",
                clientOrSource = "Startup",
                rawAmount = 135000.0,
                lkrAmount = 135000.0,
                date = baseTimestamp,
                invoiceStatus = InvoiceStatus.RECEIVED
            ),
            IncomeItem(
                streamType = IncomeStreams.FREELANCE,
                title = "UI Build",
                clientOrSource = "Client",
                rawAmount = 45000.0,
                lkrAmount = 45000.0,
                date = baseTimestamp,
                invoiceStatus = InvoiceStatus.RECEIVED
            )
        )

        val expenses = listOf(
            ExpenseItem(
                amount = 34000.0,
                category = DefaultExpenseCategories.HOUSING,
                nature = ExpenseNature.COMMITTED,
                timestamp = baseTimestamp
            ),
            ExpenseItem(
                amount = 6000.0,
                category = DefaultExpenseCategories.FOOD,
                nature = ExpenseNature.DISCRETIONARY,
                timestamp = baseTimestamp
            )
        )

        val snapshot = calculateSnapshot(
            incomes = incomes,
            expenses = expenses,
            currentEpochMs = baseTimestamp
        )

        assertEquals(180000.0, snapshot.totalIncomeLkr, 0.01)
        assertEquals(40000.0, snapshot.totalExpenseLkr, 0.01)
        assertEquals(140000.0, snapshot.netCashFlowLkr, 0.01)
        assertFalse(snapshot.isDeficit)
        assertEquals(0.7778, snapshot.savingsRate, 0.001) // 140000 / 180000
    }

    @Test
    fun `zero income with positive expenses handles negative cash flow without division by zero`() {
        val expenses = listOf(
            ExpenseItem(
                amount = 34000.0,
                category = DefaultExpenseCategories.HOUSING,
                nature = ExpenseNature.COMMITTED,
                timestamp = baseTimestamp
            )
        )

        val snapshot = calculateSnapshot(
            incomes = emptyList(),
            expenses = expenses,
            currentEpochMs = baseTimestamp
        )

        assertEquals(0.0, snapshot.totalIncomeLkr, 0.01)
        assertEquals(34000.0, snapshot.totalExpenseLkr, 0.01)
        assertEquals(-34000.0, snapshot.netCashFlowLkr, 0.01)
        assertTrue(snapshot.isDeficit)
        assertEquals(0.0, snapshot.savingsRate, 0.01) // Cannot divide by zero
        assertEquals(0.0, snapshot.safeToSpendDailyLkr, 0.01)
    }

    @Test
    fun `both totals zero produces empty clean snapshot without NaN or errors`() {
        val snapshot = calculateSnapshot(
            incomes = emptyList(),
            expenses = emptyList(),
            currentEpochMs = baseTimestamp
        )

        assertEquals(0.0, snapshot.totalIncomeLkr, 0.01)
        assertEquals(0.0, snapshot.totalExpenseLkr, 0.01)
        assertEquals(0.0, snapshot.netCashFlowLkr, 0.01)
        assertEquals(0.0, snapshot.savingsRate, 0.01)
        assertEquals(0.0, snapshot.committedExpenseLkr, 0.01)
        assertEquals(0.0, snapshot.discretionaryExpenseLkr, 0.01)
        assertEquals(0f, snapshot.committedRatio, 0.01f)
        assertEquals(0f, snapshot.discretionaryRatio, 0.01f)
        assertTrue(snapshot.incomeBySource.isEmpty())
        assertTrue(snapshot.expenseByCategory.isEmpty())
    }

    @Test
    fun `committed vs discretionary segmentation is strictly segregated`() {
        val expenses = listOf(
            ExpenseItem(amount = 34000.0, nature = ExpenseNature.COMMITTED, timestamp = baseTimestamp),
            ExpenseItem(amount = 4500.0, nature = ExpenseNature.COMMITTED, timestamp = baseTimestamp),
            ExpenseItem(amount = 1850.0, nature = ExpenseNature.DISCRETIONARY, timestamp = baseTimestamp),
            ExpenseItem(amount = 2450.0, nature = ExpenseNature.DISCRETIONARY, timestamp = baseTimestamp)
        )

        val snapshot = calculateSnapshot(
            incomes = emptyList(),
            expenses = expenses,
            currentEpochMs = baseTimestamp
        )

        assertEquals(38500.0, snapshot.committedExpenseLkr, 0.01)
        assertEquals(4300.0, snapshot.discretionaryExpenseLkr, 0.01)
        assertEquals(42800.0, snapshot.totalExpenseLkr, 0.01)
        // 38500 / 42800 = ~0.8995
        assertEquals(0.8995f, snapshot.committedRatio, 0.01f)
        assertEquals(0.1005f, snapshot.discretionaryRatio, 0.01f)
    }

    @Test
    fun `income by source percentages correctly sum up and rank descending`() {
        val incomes = listOf(
            IncomeItem(streamType = IncomeStreams.SALARY, title = "Salary", clientOrSource = "Company", rawAmount = 140000.0, lkrAmount = 140000.0, date = baseTimestamp),
            IncomeItem(streamType = IncomeStreams.ADSENSE, title = "AdSense", clientOrSource = "Google", rawAmount = 20000.0, lkrAmount = 20000.0, date = baseTimestamp),
            IncomeItem(streamType = IncomeStreams.CRYPTO, title = "ETH Trade", clientOrSource = "Binance", rawAmount = 40000.0, lkrAmount = 40000.0, date = baseTimestamp)
        )

        val snapshot = calculateSnapshot(
            incomes = incomes,
            expenses = emptyList(),
            currentEpochMs = baseTimestamp
        )

        assertEquals(3, snapshot.incomeBySource.size)
        // Top source must be Salary (140k / 200k = 70%)
        assertEquals(IncomeStreams.SALARY, snapshot.incomeBySource[0].sourceType)
        assertEquals(70.0, snapshot.incomeBySource[0].percentageOfTotal, 0.01)
        // Second source must be Crypto (40k / 200k = 20%)
        assertEquals(IncomeStreams.CRYPTO, snapshot.incomeBySource[1].sourceType)
        assertEquals(20.0, snapshot.incomeBySource[1].percentageOfTotal, 0.01)
        // Third source must be AdSense (20k / 200k = 10%)
        assertEquals(IncomeStreams.ADSENSE, snapshot.incomeBySource[2].sourceType)
        assertEquals(10.0, snapshot.incomeBySource[2].percentageOfTotal, 0.01)
    }

    @Test
    fun `pending uncollected freelance invoices are excluded from realized cash flow`() {
        val incomes = listOf(
            IncomeItem(
                streamType = IncomeStreams.FREELANCE,
                title = "Milestone 1",
                clientOrSource = "Client A",
                rawAmount = 50000.0,
                lkrAmount = 50000.0,
                date = baseTimestamp,
                invoiceStatus = InvoiceStatus.INVOICED_PENDING // Not yet received
            ),
            IncomeItem(
                streamType = IncomeStreams.SALARY,
                title = "Salary",
                clientOrSource = "Company",
                rawAmount = 135000.0,
                lkrAmount = 135000.0,
                date = baseTimestamp,
                invoiceStatus = InvoiceStatus.RECEIVED
            )
        )

        val snapshot = calculateSnapshot(
            incomes = incomes,
            expenses = emptyList(),
            currentEpochMs = baseTimestamp
        )

        assertEquals(135000.0, snapshot.totalIncomeLkr, 0.01)
    }

    @Test
    fun `safe to spend daily allowance deducts committed rent and MacBook goal pace`() {
        val incomes = listOf(
            IncomeItem(streamType = IncomeStreams.SALARY, title = "Salary", clientOrSource = "Company", rawAmount = 180000.0, lkrAmount = 180000.0, date = baseTimestamp)
        )
        val expenses = listOf(
            ExpenseItem(amount = 38500.0, nature = ExpenseNature.COMMITTED, timestamp = baseTimestamp) // Rent 34k + Gym 4.5k
        )
        // Goal requires 39,900/month
        val goal = SavingsGoal.createDefaultMacBookGoal().copy(
            deadlineEpochMs = baseTimestamp + (365L * 24 * 3600 * 1000)
        )

        val snapshot = calculateSnapshot(
            incomes = incomes,
            expenses = expenses,
            activeGoal = goal,
            daysRemainingInCycle = 30,
            currentEpochMs = baseTimestamp
        )

        assertNotNull(snapshot.activeGoalSummary)
        val monthlyGoalPace = snapshot.activeGoalSummary!!.requiredMonthlyContributionLkr
        val expectedLifestyleRemaining = (180000.0 - 38500.0 - monthlyGoalPace).coerceAtLeast(0.0)
        val expectedDaily = expectedLifestyleRemaining / 30.0

        assertEquals(expectedDaily, snapshot.safeToSpendDailyLkr, 0.1)
        assertTrue(snapshot.safeToSpendDailyLkr > 0.0)
    }

    @Test
    fun `date period filtering excludes records outside selected boundaries`() {
        val pastEpoch = baseTimestamp - (40L * 24 * 3600 * 1000) // 40 days ago
        val currentPeriod = DatePeriod(
            label = "This Month",
            startEpochMs = baseTimestamp - (10L * 24 * 3600 * 1000),
            endEpochMs = baseTimestamp + (10L * 24 * 3600 * 1000)
        )

        val expenses = listOf(
            ExpenseItem(amount = 5000.0, timestamp = pastEpoch), // Outside
            ExpenseItem(amount = 2500.0, timestamp = baseTimestamp) // Inside
        )

        val snapshot = calculateSnapshot(
            incomes = emptyList(),
            expenses = expenses,
            period = currentPeriod,
            currentEpochMs = baseTimestamp
        )

        assertEquals(2500.0, snapshot.totalExpenseLkr, 0.01)
    }
}
