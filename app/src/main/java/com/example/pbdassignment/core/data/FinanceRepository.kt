package com.example.pbdassignment.core.data

import com.example.pbdassignment.core.model.DefaultExpenseCategories
import com.example.pbdassignment.core.model.ExpenseItem
import com.example.pbdassignment.core.model.ExpenseNature
import com.example.pbdassignment.core.model.FinancialBaseline
import com.example.pbdassignment.core.model.GoalItem
import com.example.pbdassignment.core.model.IncomeItem
import com.example.pbdassignment.core.model.IncomeStreams
import com.example.pbdassignment.core.model.InvoiceStatus
import com.example.pbdassignment.core.model.PaymentSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Centralized Repository managing state for all features.
 * Pre-seeded with Kavindu Silva's authentic scenario data for demonstration.
 */
object FinanceRepository {

    private val _incomes = MutableStateFlow<List<IncomeItem>>(
        listOf(
            IncomeItem(
                streamType = IncomeStreams.SALARY,
                title = "Startup Engineering Salary",
                clientOrSource = "Tech Startup, Colombo (25th)",
                rawAmount = 135000.0,
                currency = "LKR",
                exchangeRate = 1.0,
                invoiceStatus = InvoiceStatus.RECEIVED
            ),
            IncomeItem(
                streamType = IncomeStreams.FREELANCE,
                title = "React & Tailwind Build",
                clientOrSource = "Local Retail SME",
                rawAmount = 45000.0,
                currency = "LKR",
                exchangeRate = 1.0,
                invoiceStatus = InvoiceStatus.OVERDUE,
                dueDate = System.currentTimeMillis() - (18L * 24 * 3600 * 1000)
            ),
            IncomeItem(
                streamType = IncomeStreams.ADSENSE,
                title = "Tech Blog AdSense Revenue",
                clientOrSource = "Google LLC (USD Wire)",
                rawAmount = 45.0,
                currency = "USD",
                exchangeRate = 302.50,
                invoiceStatus = InvoiceStatus.RECEIVED
            ),
            IncomeItem(
                streamType = IncomeStreams.CRYPTO,
                title = "Binance ETH/USDT Swing Trade",
                clientOrSource = "Binance Spot Ledger",
                rawAmount = 12500.0,
                currency = "LKR",
                exchangeRate = 1.0,
                invoiceStatus = InvoiceStatus.RECEIVED
            )
        )
    )
    val incomes: StateFlow<List<IncomeItem>> = _incomes.asStateFlow()

    private val _expenses = MutableStateFlow<List<ExpenseItem>>(
        listOf(
            ExpenseItem(
                amount = 34000.0,
                category = DefaultExpenseCategories.HOUSING,
                nature = ExpenseNature.COMMITTED,
                paymentSource = PaymentSource.PRIMARY_CARD,
                merchantPreset = "Rent",
                notes = "Monthly Apartment Rent"
            ),
            ExpenseItem(
                amount = 4500.0,
                category = DefaultExpenseCategories.HEALTH_GYM,
                nature = ExpenseNature.COMMITTED,
                paymentSource = PaymentSource.SECONDARY_ACCOUNT,
                merchantPreset = "Gym",
                notes = "Auto-debited from secondary account"
            ),
            ExpenseItem(
                amount = 1850.0,
                category = DefaultExpenseCategories.TRANSPORT,
                nature = ExpenseNature.DISCRETIONARY,
                paymentSource = PaymentSource.PRIMARY_CARD,
                merchantPreset = "PickMe",
                notes = "PickMe ride back from office"
            ),
            ExpenseItem(
                amount = 3200.0,
                category = DefaultExpenseCategories.FOOD,
                nature = ExpenseNature.DISCRETIONARY,
                paymentSource = PaymentSource.PRIMARY_CARD,
                merchantPreset = "UberEats",
                notes = "UberEats dinner order"
            ),
            ExpenseItem(
                amount = 2400.0,
                category = DefaultExpenseCategories.FOOD,
                nature = ExpenseNature.DISCRETIONARY,
                paymentSource = PaymentSource.CASH,
                merchantPreset = "Coffee & Dining",
                notes = "Artisan cafe with startup team"
            )
        )
    )
    val expenses: StateFlow<List<ExpenseItem>> = _expenses.asStateFlow()

    private val _goal = MutableStateFlow(
        GoalItem(
            title = "MacBook Pro M4 (14-inch)",
            targetAmount = 490000.0,
            currentSaved = 11200.0,
            targetMonths = 12
        )
    )
    val goal: StateFlow<GoalItem> = _goal.asStateFlow()

    private val _baseline = MutableStateFlow(FinancialBaseline())
    val baseline: StateFlow<FinancialBaseline> = _baseline.asStateFlow()

    // --- Income Methods ---
    fun addIncome(income: IncomeItem) {
        _incomes.update { current ->
            if (current.any { it.id == income.id }) current else listOf(income) + current
        }
        com.example.pbdassignment.core.firebase.FirebaseManager.syncIncome(income)
    }

    fun updateInvoiceStatus(id: String, newStatus: InvoiceStatus) {
        _incomes.update { list ->
            list.map { if (it.id == id) it.copy(invoiceStatus = newStatus) else it }
        }
        _incomes.value.find { it.id == id }?.let {
            com.example.pbdassignment.core.firebase.FirebaseManager.syncIncome(it)
        }
    }

    fun deleteIncome(id: String) {
        _incomes.update { list -> list.filterNot { it.id == id } }
        com.example.pbdassignment.core.firebase.FirebaseManager.deleteIncome(id)
    }

    // --- Expense Methods ---
    fun addExpense(expense: ExpenseItem) {
        upsertLocalExpense(expense)
        com.example.pbdassignment.core.firebase.FirebaseManager.syncExpense(expense)
    }

    fun upsertLocalExpense(expense: ExpenseItem) {
        _expenses.update { current ->
            if (current.any { it.id == expense.id }) {
                current.map { if (it.id == expense.id) expense else it }
            } else {
                listOf(expense) + current
            }
        }
    }

    fun removeLocalExpense(id: String) {
        _expenses.update { list -> list.filterNot { it.id == id } }
    }

    fun deleteExpense(id: String) {
        removeLocalExpense(id)
        com.example.pbdassignment.core.firebase.FirebaseManager.deleteExpense(id)
    }

    // --- Goal Methods ---
    fun allocateToGoal(amount: Double) {
        if (amount <= 0) return
        _goal.update { current ->
            current.copy(currentSaved = current.currentSaved + amount)
        }
        com.example.pbdassignment.core.firebase.FirebaseManager.syncGoal(_goal.value)
    }

    fun updateGoalTarget(newTarget: Double) {
        if (newTarget <= 0) return
        _goal.update { current ->
            current.copy(targetAmount = newTarget)
        }
        com.example.pbdassignment.core.firebase.FirebaseManager.syncGoal(_goal.value)
    }

    fun updateFullGoal(title: String, targetAmount: Double, currentSaved: Double, months: Int) {
        _goal.update {
            it.copy(
                title = title.ifBlank { it.title },
                targetAmount = if (targetAmount > 0) targetAmount else it.targetAmount,
                currentSaved = if (currentSaved >= 0) currentSaved else it.currentSaved,
                targetMonths = if (months > 0) months else it.targetMonths
            )
        }
        com.example.pbdassignment.core.firebase.FirebaseManager.syncGoal(_goal.value)
    }

    // --- Financial Baseline & Custom Rules Methods ---
    fun updateBaseline(newBaseline: FinancialBaseline) {
        _baseline.value = newBaseline
        com.example.pbdassignment.core.firebase.FirebaseManager.syncBaseline(newBaseline)
    }

    fun updateLocalBaseline(newBaseline: FinancialBaseline) {
        _baseline.value = newBaseline
    }

    fun addCustomRule(ruleText: String) {
        if (ruleText.isBlank()) return
        _baseline.update { current ->
            current.copy(rules = current.rules + ruleText.trim())
        }
        com.example.pbdassignment.core.firebase.FirebaseManager.syncBaseline(_baseline.value)
    }

    fun removeCustomRule(ruleText: String) {
        _baseline.update { current ->
            current.copy(rules = current.rules.filterNot { it == ruleText })
        }
        com.example.pbdassignment.core.firebase.FirebaseManager.syncBaseline(_baseline.value)
    }
}
