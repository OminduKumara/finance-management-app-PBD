
package com.example.pbdassignment.feature.expense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }
}
