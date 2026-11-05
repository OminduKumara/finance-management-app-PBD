
package com.example.pbdassignment.feature.expense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.core.data.FinanceRepository
import com.example.pbdassignment.core.model.ExpenseItem
import com.example.pbdassignment.core.model.ExpenseNature
import com.example.pbdassignment.core.model.PaymentSource
import com.example.pbdassignment.feature.expense.domain.Expense
import com.example.pbdassignment.feature.expense.domain.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses: StateFlow<List<Expense>> =
        repository.observeExpenses().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.addExpense(expense)
            com.example.pbdassignment.core.firebase.FirebaseManager.syncExpense(expense)
            syncToFinanceRepository(expense)
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id)
            com.example.pbdassignment.core.firebase.FirebaseManager.deleteExpense(id)
            FinanceRepository.deleteExpense(id)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
            com.example.pbdassignment.core.firebase.FirebaseManager.syncExpense(expense)
            FinanceRepository.deleteExpense(expense.id)
            syncToFinanceRepository(expense)
        }
    }

    private fun syncToFinanceRepository(expense: Expense) {
        FinanceRepository.addExpense(
            ExpenseItem(
                id = expense.id,
                amount = expense.amount,
                category = expense.categoryName,
                nature = if (expense.isCommitted) ExpenseNature.COMMITTED else ExpenseNature.DISCRETIONARY,
                paymentSource = when (expense.paymentMethod.lowercase()) {
                    "credit card", "credit_card", "card" -> PaymentSource.PRIMARY_CARD
                    "bank transfer", "debit card" -> PaymentSource.SECONDARY_ACCOUNT
                    else -> PaymentSource.CASH
                },
                merchantPreset = expense.title,
                notes = expense.title
            )
        )
    }
}
