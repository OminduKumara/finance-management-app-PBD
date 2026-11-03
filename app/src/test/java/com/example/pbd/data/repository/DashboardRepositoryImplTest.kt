package com.example.pbd.data.repository

import com.example.pbd.core.data.FinanceRepository
import com.example.pbd.core.model.DatePeriod
import com.example.pbd.core.model.DefaultExpenseCategories
import com.example.pbd.core.model.ExpenseItem
import com.example.pbd.core.model.ExpenseNature
import com.example.pbd.core.model.IncomeItem
import com.example.pbd.core.model.IncomeStreams
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DashboardRepositoryImplTest {

    private lateinit var repository: DashboardRepositoryImpl

    @Before
    fun setup() {
        repository = DashboardRepositoryImpl()
    }

    @Test
    fun `getFinancialSnapshot returns combined real-time snapshot with analytics`() = runBlocking {
        val snapshot = repository.getFinancialSnapshot(DatePeriod.allTime())

        assertNotNull(snapshot)
        assertTrue("Total income should reflect seed data", snapshot.totalIncomeLkr > 0)
        assertTrue("Total expense should reflect seed data", snapshot.totalExpenseLkr > 0)
        assertTrue("Net cash flow should be calculated", snapshot.netCashFlowLkr != 0.0)

        // Verify Category Analytics
        assertFalse("Expense categories should not be empty", snapshot.expenseByCategory.isEmpty())
        assertTrue("Categories should include Housing or Food", snapshot.expenseByCategory.any {
            it.categoryName == DefaultExpenseCategories.HOUSING || it.categoryName == DefaultExpenseCategories.FOOD
        })

        // Verify Committed vs Discretionary
        assertTrue("Committed expenses should be tracked", snapshot.committedExpenseLkr > 0)
        assertTrue("Discretionary expenses should be tracked", snapshot.discretionaryExpenseLkr > 0)
        assertEquals(1.0f, snapshot.committedRatio + snapshot.discretionaryRatio, 0.01f)

        // Verify Income Sources
        assertFalse("Income sources should not be empty", snapshot.incomeBySource.isEmpty())
        assertTrue("Income should include Salary", snapshot.incomeBySource.any {
            it.sourceType == IncomeStreams.SALARY
        })
    }
}
