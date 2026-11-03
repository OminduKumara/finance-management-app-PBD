package com.example.pbd.feature_expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbd.core.data.FinanceRepository
import com.example.pbd.core.model.ExpenseItem
import com.example.pbd.core.model.ExpenseNature
import com.example.pbd.core.model.PaymentSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ExpenseUiState(
    val items: List<ExpenseItem> = emptyList(),
    val totalExpenseLkr: Double = 0.0,
    val committedLkr: Double = 0.0,
    val discretionaryLkr: Double = 0.0,
    val diningTotalLkr: Double = 0.0,
    val isDiningAlertActive: Boolean = false,
    val selectedCategory: String? = null
)

class ExpenseViewModel : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ExpenseUiState> = combine(
        FinanceRepository.expenses,
        _selectedCategory
    ) { expenses, categoryFilter ->
        val filtered = if (categoryFilter == null) expenses else expenses.filter { it.category.equals(categoryFilter, ignoreCase = true) }
        val total = expenses.sumOf { it.amount }
        val committed = expenses.filter { it.nature == ExpenseNature.COMMITTED }.sumOf { it.amount }
        val discretionary = expenses.filter { it.nature == ExpenseNature.DISCRETIONARY }.sumOf { it.amount }
        val diningTotal = expenses.filter {
            it.category.contains("Food", ignoreCase = true) || it.category.contains("Dining", ignoreCase = true)
        }.sumOf { it.amount }
        val isAlert = diningTotal >= 15000.0 // Threshold warning

        ExpenseUiState(
            items = filtered,
            totalExpenseLkr = total,
            committedLkr = committed,
            discretionaryLkr = discretionary,
            diningTotalLkr = diningTotal,
            isDiningAlertActive = isAlert,
            selectedCategory = categoryFilter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpenseUiState()
    )

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun quickAddExpense(
        amount: Double,
        category: String,
        nature: ExpenseNature,
        source: PaymentSource,
        merchantPreset: String? = null,
        notes: String = ""
    ) {
        val expense = ExpenseItem(
            amount = amount,
            category = category.ifBlank { "General" },
            nature = nature,
            paymentSource = source,
            merchantPreset = merchantPreset,
            notes = notes
        )
        FinanceRepository.addExpense(expense)
    }

    fun deleteExpense(id: String) {
        FinanceRepository.deleteExpense(id)
    }
}
