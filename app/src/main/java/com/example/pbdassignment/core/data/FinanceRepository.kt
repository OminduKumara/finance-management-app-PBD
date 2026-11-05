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
import com.example.pbdassignment.feature.income.data.IncomeRepositoryProvider
import com.example.pbdassignment.feature.income.data.LocalIncomeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Centralized Repository managing state for all features.
 * Starts clean with empty lists and syncs with Room and Firebase Firestore.
 */
object FinanceRepository {

    private val _incomes = MutableStateFlow<List<IncomeItem>>(emptyList())
    val incomes: StateFlow<List<IncomeItem>> = _incomes.asStateFlow()

    private val _expenses = MutableStateFlow<List<ExpenseItem>>(emptyList())
    val expenses: StateFlow<List<ExpenseItem>> = _expenses.asStateFlow()

    private val _goal = MutableStateFlow(GoalItem())
    val goal: StateFlow<GoalItem> = _goal.asStateFlow()

    private val _baseline = MutableStateFlow(FinancialBaseline())
    val baseline: StateFlow<FinancialBaseline> = _baseline.asStateFlow()

    fun clearLocalData() {
        _incomes.value = emptyList()
        _expenses.value = emptyList()
        _goal.value = GoalItem()
        _baseline.value = FinancialBaseline()
    }

    fun upsertLocalIncome(income: IncomeItem) {
        _incomes.update { current ->
            if (current.any { it.id == income.id }) {
                current.map { if (it.id == income.id) income else it }
            } else {
                listOf(income) + current
            }
        }
    }

    // --- Income Methods ---
    fun addIncome(income: IncomeItem) {
        upsertLocalIncome(income)
        com.example.pbdassignment.core.firebase.FirebaseManager.syncIncome(income)
        IncomeRepositoryProvider.instance?.let { repo: LocalIncomeRepository ->
            CoroutineScope(Dispatchers.IO).launch {
                repo.addIncome(income)
            }
        }
    }

    fun updateInvoiceStatus(id: String, newStatus: InvoiceStatus) {
        _incomes.update { list ->
            list.map { if (it.id == id) it.copy(invoiceStatus = newStatus) else it }
        }
        val updated = _incomes.value.find { it.id == id }
        if (updated != null) {
            com.example.pbdassignment.core.firebase.FirebaseManager.syncIncome(updated)
            IncomeRepositoryProvider.instance?.let { repo: LocalIncomeRepository ->
                CoroutineScope(Dispatchers.IO).launch {
                    repo.updateIncome(updated)
                }
            }
        }
    }

    fun deleteIncome(id: String) {
        _incomes.update { list -> list.filterNot { it.id == id } }
        com.example.pbdassignment.core.firebase.FirebaseManager.deleteIncome(id)
        IncomeRepositoryProvider.instance?.let { repo: LocalIncomeRepository ->
            CoroutineScope(Dispatchers.IO).launch {
                repo.deleteIncome(id)
            }
        }
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
                targetAmount = if (targetAmount >= 0) targetAmount else it.targetAmount,
                currentSaved = if (currentSaved >= 0) currentSaved else it.currentSaved,
                targetMonths = if (months > 0) months else it.targetMonths
            )
        }
        com.example.pbdassignment.core.firebase.FirebaseManager.syncGoal(_goal.value)
    }

    fun updateLocalGoal(goalItem: GoalItem) {
        _goal.value = goalItem
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
