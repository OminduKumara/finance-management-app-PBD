package com.example.pbdassignment.feature.recurring.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pbdassignment.feature.recurring.domain.RecurringExpenseRepository

class RecurringExpenseViewModelFactory(
    private val repository: RecurringExpenseRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecurringExpenseViewModel::class.java)) {
            return RecurringExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}