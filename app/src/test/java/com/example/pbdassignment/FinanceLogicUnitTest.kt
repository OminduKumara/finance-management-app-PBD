package com.example.pbdassignment

import com.example.pbdassignment.core.model.DefaultExpenseCategories
import com.example.pbdassignment.core.model.ExpenseItem
import com.example.pbdassignment.core.model.ExpenseNature
import com.example.pbdassignment.core.model.GoalItem
import com.example.pbdassignment.core.model.IncomeItem
import com.example.pbdassignment.core.model.IncomeStreams
import com.example.pbdassignment.core.model.InvoiceStatus
import com.example.pbdassignment.core.model.PaymentSource
import com.example.pbdassignment.feature.income.data.toIncomeEntity
import com.example.pbdassignment.feature.income.data.toIncomeItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinanceLogicUnitTest {

    @Test
    fun testUsdToLkrConversion() {
        val rawUsd = 45.0
        val rate = 302.50
        val income = IncomeItem(
            streamType = IncomeStreams.ADSENSE,
            title = "AdSense USD",
            clientOrSource = "Google",
            rawAmount = rawUsd,
            currency = "USD",
            exchangeRate = rate
        )
        val expectedLkr = 45.0 * 302.50
        assertEquals(expectedLkr, income.lkrAmount, 0.01)
    }

    @Test
    fun testCustomIncomeStreamAndExpenseCategorySupport() {
        // Verification that user can enter any arbitrary stream & category
        val customIncome = IncomeItem(
            streamType = "Upwork Consulting",
            title = "AI Model Fine-tuning",
            clientOrSource = "Client US",
            rawAmount = 500.0,
            currency = "USD",
            exchangeRate = 305.0
        )
        assertEquals("Upwork Consulting", customIncome.streamType)
        assertEquals(152500.0, customIncome.lkrAmount, 0.01)

        val customExpense = ExpenseItem(
            amount = 14500.0,
            category = "Ergonomic Mechanical Keyboard",
            nature = ExpenseNature.DISCRETIONARY,
            paymentSource = PaymentSource.PRIMARY_CARD
        )
        assertEquals("Ergonomic Mechanical Keyboard", customExpense.category)
    }

    @Test
    fun testGoalRunwayCalculations() {
        val goal = GoalItem(
            title = "MacBook Pro M4",
            targetAmount = 490000.0,
            currentSaved = 11200.0,
            targetMonths = 12
        )
        val expectedDeficit = 478800.0
        assertEquals(expectedDeficit, goal.remainingDeficit, 0.01)

        val expectedMonthlyRate = 478800.0 / 12.0 // 39,900.0
        assertEquals(39900.0, goal.requiredMonthlySavings, 0.01)

        val expectedDailyRate = 39900.0 / 30.0 // 1,330.0
        assertEquals(1330.0, goal.requiredDailySavings, 0.01)
    }

    @Test
    fun testDailySafeToSpendBurnRateCalculation() {
        val realizedIncome = 180000.0
        val committedExpenses = 38500.0 // Rent 34k + Gym 4.5k
        val monthlyGoalSavings = 39900.0 // MacBook fund

        val remainingForLifestyle = realizedIncome - committedExpenses - monthlyGoalSavings
        val dailySafeToSpend = remainingForLifestyle / 30.0 // 101,600 / 30 = 3386.66...

        assertEquals(3386.67, dailySafeToSpend, 0.1)
        assertTrue("Daily safe to spend should be positive", dailySafeToSpend > 0)
    }

    @Test
    fun testDiscretionaryLeakAlert() {
        val expenses = listOf(
            ExpenseItem(amount = 12000.0, category = DefaultExpenseCategories.FOOD, nature = ExpenseNature.DISCRETIONARY, paymentSource = PaymentSource.PRIMARY_CARD),
            ExpenseItem(amount = 9000.0, category = DefaultExpenseCategories.FOOD, nature = ExpenseNature.DISCRETIONARY, paymentSource = PaymentSource.CASH)
        )
        val diningTotal = expenses.sumOf { it.amount }
        val threshold = 15000.0
        val isLeakAlert = diningTotal >= threshold

        assertTrue("Dining total should trigger spending leak alert", isLeakAlert)
    }

    @Test
    fun testIncomeEntityMappingRoundTrip() {
        val original = IncomeItem(
            id = "income_test_123",
            streamType = IncomeStreams.FREELANCE,
            title = "Mobile App Design",
            clientOrSource = "Fintech Client",
            rawAmount = 1200.0,
            currency = "USD",
            exchangeRate = 300.0,
            lkrAmount = 360000.0,
            date = 1700000000000L,
            invoiceStatus = InvoiceStatus.SETTLED,
            dueDate = 1700500000000L,
            isSweptToGoal = true,
            goalSweptAmount = 180000.0
        )

        val entity = original.toIncomeEntity()
        assertEquals(original.id, entity.id)
        assertEquals(original.streamType, entity.streamType)
        assertEquals(original.title, entity.title)
        assertEquals(original.clientOrSource, entity.clientOrSource)
        assertEquals(original.rawAmount, entity.rawAmount, 0.001)
        assertEquals(original.currency, entity.currency)
        assertEquals(original.exchangeRate, entity.exchangeRate, 0.001)
        assertEquals(original.lkrAmount, entity.lkrAmount, 0.001)
        assertEquals(original.date, entity.date)
        assertEquals(original.invoiceStatus.name, entity.invoiceStatus)
        assertEquals(original.dueDate, entity.dueDate)
        assertEquals(original.isSweptToGoal, entity.isSweptToGoal)
        assertEquals(original.goalSweptAmount, entity.goalSweptAmount, 0.001)

        val restored = entity.toIncomeItem()
        assertEquals(original, restored)
    }
}
