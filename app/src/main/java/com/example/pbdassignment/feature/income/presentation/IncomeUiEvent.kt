package com.example.pbdassignment.feature.income.presentation

import com.example.pbdassignment.feature.income.domain.CryptoTradeRecord
import com.example.pbdassignment.feature.income.domain.IncomeRecord

sealed class IncomeUiEvent {
    data class AddIncome(val income: IncomeRecord) : IncomeUiEvent()
    data class AddCryptoTrade(val trade: CryptoTradeRecord) : IncomeUiEvent()
    data class DeleteIncome(val incomeId: String) : IncomeUiEvent()
    data class SelectMonth(val month: Int, val year: Int) : IncomeUiEvent()
    object ClearMessage : IncomeUiEvent()
}