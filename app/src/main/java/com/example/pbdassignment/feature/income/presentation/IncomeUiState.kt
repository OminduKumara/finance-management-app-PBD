package com.example.pbdassignment.feature.income.presentation

import com.example.pbdassignment.feature.income.domain.IncomeRecord

data class IncomeUiState(
    val isLoading: Boolean = true,
    val incomeList: List<IncomeRecord> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)