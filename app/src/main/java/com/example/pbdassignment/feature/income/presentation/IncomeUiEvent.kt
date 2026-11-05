package com.example.pbdassignment.feature.income.presentation

import com.example.pbdassignment.feature.income.domain.IncomeSource
import com.example.pbdassignment.feature.income.domain.MilestoneStatus

sealed class IncomeUiEvent {

    object OpenAddIncomeSheet : IncomeUiEvent()
    object DismissAddIncomeSheet : IncomeUiEvent()
    object OpenCryptoSheet : IncomeUiEvent()
    object DismissCryptoSheet : IncomeUiEvent()

    data class SelectSource(val source: IncomeSource) : IncomeUiEvent()

    data class FormTitleChanged(val value: String) : IncomeUiEvent()
    data class FormNoteChanged(val value: String) : IncomeUiEvent()

    data class FormBaseSalaryChanged(val value: String) : IncomeUiEvent()
    data class FormBonusChanged(val value: String) : IncomeUiEvent()

    data class FormClientNameChanged(val value: String) : IncomeUiEvent()
    data class FormInvoiceNumberChanged(val value: String) : IncomeUiEvent()
    data class FormFreelanceAmountChanged(val value: String) : IncomeUiEvent()
    data class FormMilestoneStatusChanged(val status: MilestoneStatus) : IncomeUiEvent()

    data class FormUsdAmountChanged(val value: String) : IncomeUiEvent()
    data class FormExchangeRateChanged(val value: String) : IncomeUiEvent()

    data class FormAssetSymbolChanged(val value: String) : IncomeUiEvent()
    data class FormBuyPriceChanged(val value: String) : IncomeUiEvent()
    data class FormSellPriceChanged(val value: String) : IncomeUiEvent()
    data class FormQuantityChanged(val value: String) : IncomeUiEvent()
    data class FormCryptoRateChanged(val value: String) : IncomeUiEvent()

    object SubmitIncome : IncomeUiEvent()
    object SubmitCryptoTrade : IncomeUiEvent()
    data class DeleteIncome(val incomeId: String) : IncomeUiEvent()

    data class SelectMonth(val month: Int, val year: Int) : IncomeUiEvent()

    object ClearMessage : IncomeUiEvent()
}