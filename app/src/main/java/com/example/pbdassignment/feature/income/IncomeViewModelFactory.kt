package com.example.pbdassignment.feature.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pbdassignment.feature.income.data.LocalIncomeRepository

class IncomeViewModelFactory(
    private val repository: LocalIncomeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IncomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

