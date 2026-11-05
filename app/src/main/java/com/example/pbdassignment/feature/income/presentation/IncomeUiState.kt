package com.example.pbdassignment.feature.income.presentation

import com.example.pbdassignment.feature.income.domain.CryptoTradeRecord
import com.example.pbdassignment.feature.income.domain.IncomeRecord
import com.example.pbdassignment.feature.income.domain.IncomeSource
import com.example.pbdassignment.feature.income.domain.MilestoneStatus

data class IncomeUiState(

    val isLoading: Boolean = true,

    val incomeList: List<IncomeRecord> = emptyList(),
    val cryptoTrades: List<CryptoTradeRecord> = emptyList(),

    val totalReceivedLkr: Double = 0.0,
    val salarySumLkr: Double = 0.0,
    val freelanceSumLkr: Double = 0.0,
    val adsenseSumLkr: Double = 0.0,
    val cryptoPnLLkr: Double = 0.0,
    val pendingFreelanceLkr: Double = 0.0,
    val pendingFreelanceCount: Int = 0,

    val selectedMonth: Int = java.util.Calendar.getInstance()
        .get(java.util.Calendar.MONTH),
    val selectedYear: Int = java.util.Calendar.getInstance()
        .get(java.util.Calendar.YEAR),

    val showAddIncomeSheet: Boolean = false,
    val showCryptoSheet: Boolean = false,

    val formSelectedSource: IncomeSource = IncomeSource.SALARY,
    val formTitle: String = "",
    val formTitleError: String? = null,
    val formNote: String = "",

    val formBaseSalaryLkr: String = "",
    val formBonusLkr: String = "",

    val formClientName: String = "",
    val formInvoiceNumber: String = "",
    val formFreelanceAmountLkr: String = "",
    val formMilestoneStatus: MilestoneStatus = MilestoneStatus.PENDING,

    val formUsdAmount: String = "",
    val formExchangeRate: String = "305.00",
    val formLkrPreview: String = "",

    val formAssetSymbol: String = "",
    val formBuyPriceUsd: String = "",
    val formSellPriceUsd: String = "",
    val formQuantity: String = "",
    val formCryptoExchangeRate: String = "305.00",
    val formPnLPreview: String = "",

    val errorMessage: String? = null,
    val successMessage: String? = null
)