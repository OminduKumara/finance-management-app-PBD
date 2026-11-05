package com.example.pbdassignment.feature.recurring.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.feature.recurring.domain.RecurringExpense
import com.example.pbdassignment.feature.recurring.domain.RecurringExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecurringExpenseViewModel(
    private val repository: RecurringExpenseRepository
) : ViewModel() {

    val recurringExpenses: StateFlow<List<RecurringExpense>> = repository.observeRecurringExpenses().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addRecurringExpense(expense: RecurringExpense) {
        viewModelScope.launch {
            repository.addRecurringExpense(expense)
        }
    }

    fun updateRecurringExpense(expense: RecurringExpense) {
        viewModelScope.launch {
            repository.updateRecurringExpense(expense)
        }
    }

    fun deleteRecurringExpense(id: String) {
        viewModelScope.launch {
            repository.deleteRecurringExpense(id)
        }
    }
}