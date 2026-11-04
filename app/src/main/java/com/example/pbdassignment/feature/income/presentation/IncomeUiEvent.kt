package com.example.pbdassignment.feature.income.presentation

import com.example.pbdassignment.feature.income.domain.IncomeRecord

sealed class IncomeUiEvent {
    data class AddIncome(val income: IncomeRecord) : IncomeUiEvent()
    data class DeleteIncome(val incomeId: String) : IncomeUiEvent()
    object ClearMessage : IncomeUiEvent()
}