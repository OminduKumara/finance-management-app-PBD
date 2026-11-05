package com.example.pbdassignment.feature.income.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pbdassignment.feature.authentication.data.AuthRepositoryImpl
import com.example.pbdassignment.feature.authentication.domain.AuthRepository
import com.example.pbdassignment.feature.income.data.IncomeRepositoryImpl
import com.example.pbdassignment.feature.income.domain.IncomeRepository

class IncomeViewModelFactory(
    private val incomeRepository: IncomeRepository = IncomeRepositoryImpl(),
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncomeViewModel::class.java)) {
            return IncomeViewModel(incomeRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
