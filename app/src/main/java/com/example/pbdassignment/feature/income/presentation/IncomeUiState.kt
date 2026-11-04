package com.example.pbdassignment.feature.income.presentation

import com.example.pbdassignment.feature.income.domain.CryptoTradeRecord
import com.example.pbdassignment.feature.income.domain.IncomeRecord

data class IncomeUiState(
    val isLoading: Boolean = true,
    val incomeList: List<IncomeRecord> = emptyList(),
    val cryptoTrades: List<CryptoTradeRecord> = emptyList(),

    val totalReceivedLkr: Double = 0.0,
    val salarySumLkr: Double = 0.0,
    val freelanceSumLkr: Double = 0.0,
    val adsenseSumLkr: Double = 0.0,
    val cryptoPnLLkr: Double = 0.0,           // Can be negative (loss)
    val pendingFreelanceLkr: Double = 0.0,
    val pendingFreelanceCount: Int = 0,

    val selectedMonth: Int = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH),
    val selectedYear: Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
    val errorMessage: String? = null,
    val successMessage: String? = null
)