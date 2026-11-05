package com.example.pbdassignment.feature.income.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.core.common.Resource
import com.example.pbdassignment.core.data.FinanceRepository
import com.example.pbdassignment.core.model.IncomeItem
import com.example.pbdassignment.core.model.InvoiceStatus
import com.example.pbdassignment.feature.authentication.domain.AuthRepository
import com.example.pbdassignment.feature.income.domain.AddIncome
import com.example.pbdassignment.feature.income.domain.CalculateRealisedPnL
import com.example.pbdassignment.feature.income.domain.ConvertToLkr
import com.example.pbdassignment.feature.income.domain.CryptoTradeRecord
import com.example.pbdassignment.feature.income.domain.CurrencyType
import com.example.pbdassignment.feature.income.domain.GetAggregatedIncome
import com.example.pbdassignment.feature.income.domain.IncomeRecord
import com.example.pbdassignment.feature.income.domain.IncomeRepository
import com.example.pbdassignment.feature.income.domain.IncomeSource
import com.example.pbdassignment.feature.income.domain.MilestoneStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

class IncomeViewModel(
    private val incomeRepository: IncomeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val addIncomeUseCase = AddIncome(
        incomeRepository,
        ConvertToLkr(),
        CalculateRealisedPnL()
    )
    private val aggregateUseCase = GetAggregatedIncome()
    private val fmt = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }
    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()
    private var currentUserId: String? = null
    init {
        observeUserAndLoadData()
    }
    // ── Auth + Data observation ───────────────────────────────────────────
    private fun observeUserAndLoadData() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                currentUserId = user?.uid
                if (user != null) observeIncomeData(user.uid)
                else _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    private fun observeIncomeData(userId: String) {
        viewModelScope.launch {
            combine(
                incomeRepository.getIncomes(userId),
                incomeRepository.getCryptoTrades(userId)
            ) { incomes, trades -> Pair(incomes, trades) }
                .collect { (incomes, trades) ->
                    val agg = aggregateUseCase(
                        incomes = incomes,
                        cryptoTrades = trades,
                        month = _uiState.value.selectedMonth,
                        year = _uiState.value.selectedYear
                    )
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            incomeList = incomes,
                            cryptoTrades = trades,
                            totalReceivedLkr = agg.totalReceivedLkr,
                            salarySumLkr = agg.salarySumLkr,
                            freelanceSumLkr = agg.freelanceSumLkr,
                            adsenseSumLkr = agg.adsenseSumLkr,
                            cryptoPnLLkr = agg.cryptoPnLSumLkr,
                            pendingFreelanceLkr = agg.pendingFreelanceLkr,
                            pendingFreelanceCount = agg.pendingFreelanceCount
                        )
                    }
                }
        }
    }
    // ── Single event handler ──────────────────────────────────────────────
    fun onEvent(event: IncomeUiEvent) {
        when (event) {
            // Sheet control
            is IncomeUiEvent.OpenAddIncomeSheet ->
                _uiState.update { it.copy(showAddIncomeSheet = true) }
            is IncomeUiEvent.DismissAddIncomeSheet ->
                _uiState.update { it.copy(showAddIncomeSheet = false) }
            is IncomeUiEvent.OpenCryptoSheet ->
                _uiState.update { it.copy(showCryptoSheet = true) }
            is IncomeUiEvent.DismissCryptoSheet ->
                _uiState.update { it.copy(showCryptoSheet = false) }
            // Source selection resets relevant preview
            is IncomeUiEvent.SelectSource ->
                _uiState.update { it.copy(formSelectedSource = event.source, formLkrPreview = "", formPnLPreview = "") }
            // Shared fields
            is IncomeUiEvent.FormTitleChanged ->
                _uiState.update { it.copy(formTitle = event.value, formTitleError = null) }
            is IncomeUiEvent.FormNoteChanged ->
                _uiState.update { it.copy(formNote = event.value) }
            // Salary fields
            is IncomeUiEvent.FormBaseSalaryChanged ->
                _uiState.update { it.copy(formBaseSalaryLkr = event.value) }
            is IncomeUiEvent.FormBonusChanged ->
                _uiState.update { it.copy(formBonusLkr = event.value) }
            // Freelance fields
            is IncomeUiEvent.FormClientNameChanged ->
                _uiState.update { it.copy(formClientName = event.value) }
            is IncomeUiEvent.FormInvoiceNumberChanged ->
                _uiState.update { it.copy(formInvoiceNumber = event.value) }
            is IncomeUiEvent.FormFreelanceAmountChanged ->
                _uiState.update { it.copy(formFreelanceAmountLkr = event.value) }
            is IncomeUiEvent.FormMilestoneStatusChanged ->
                _uiState.update { it.copy(formMilestoneStatus = event.status) }
            // AdSense fields – recalculate preview on each change
            is IncomeUiEvent.FormUsdAmountChanged -> {
                _uiState.update { it.copy(formUsdAmount = event.value) }
                recalcLkrPreview()
            }
            is IncomeUiEvent.FormExchangeRateChanged -> {
                _uiState.update { it.copy(formExchangeRate = event.value) }
                recalcLkrPreview()
            }
            // Crypto fields – recalculate P&L preview on each change
            is IncomeUiEvent.FormAssetSymbolChanged ->
                _uiState.update { it.copy(formAssetSymbol = event.value) }
            is IncomeUiEvent.FormBuyPriceChanged -> {
                _uiState.update { it.copy(formBuyPriceUsd = event.value) }
                recalcPnLPreview()
            }
            is IncomeUiEvent.FormSellPriceChanged -> {
                _uiState.update { it.copy(formSellPriceUsd = event.value) }
                recalcPnLPreview()
            }
            is IncomeUiEvent.FormQuantityChanged -> {
                _uiState.update { it.copy(formQuantity = event.value) }
                recalcPnLPreview()
            }
            is IncomeUiEvent.FormCryptoRateChanged -> {
                _uiState.update { it.copy(formCryptoExchangeRate = event.value) }
                recalcPnLPreview()
            }
            // Submit
            is IncomeUiEvent.SubmitIncome -> submitIncome()
            is IncomeUiEvent.SubmitCryptoTrade -> submitCryptoTrade()
            // Delete
            is IncomeUiEvent.DeleteIncome -> deleteIncome(event.incomeId)
            // Month
            is IncomeUiEvent.SelectMonth -> {
                _uiState.update { it.copy(selectedMonth = event.month, selectedYear = event.year) }
                currentUserId?.let { observeIncomeData(it) }
            }
            // Clear
            is IncomeUiEvent.ClearMessage ->
                _uiState.update { it.copy(errorMessage = null, successMessage = null) }
        }
    }
    // ── Live previews ─────────────────────────────────────────────────────
    private fun recalcLkrPreview() {
        val s = _uiState.value
        val usd = s.formUsdAmount.toBigDecimalOrNull() ?: return
        val rate = s.formExchangeRate.toBigDecimalOrNull() ?: return
        val lkr = usd.multiply(rate).setScale(2, RoundingMode.HALF_UP)
        _uiState.update { it.copy(formLkrPreview = "≈ LKR ${fmt.format(lkr)}") }
    }
    private fun recalcPnLPreview() {
        val s = _uiState.value
        val buy = s.formBuyPriceUsd.toBigDecimalOrNull() ?: return
        val sell = s.formSellPriceUsd.toBigDecimalOrNull() ?: return
        val qty = s.formQuantity.toBigDecimalOrNull() ?: return
        val rate = s.formCryptoExchangeRate.toBigDecimalOrNull() ?: return
        val pnlUsd = sell.subtract(buy).multiply(qty).setScale(8, RoundingMode.HALF_UP)
        val pnlLkr = pnlUsd.multiply(rate).setScale(2, RoundingMode.HALF_UP)
        val sign = if (pnlLkr >= BigDecimal.ZERO) "+" else ""
        _uiState.update { it.copy(formPnLPreview = "P&L ≈ ${sign}LKR ${fmt.format(pnlLkr)}") }
    }
    // ── Submit income ─────────────────────────────────────────────────────
    private fun submitIncome() {
        val s = _uiState.value
        val uid = currentUserId ?: run {
            _uiState.update { it.copy(errorMessage = "User not authenticated") }; return
        }
        // Build IncomeRecord based on selected source
        val record = when (s.formSelectedSource) {
            IncomeSource.SALARY -> {
                val base = s.formBaseSalaryLkr.toDoubleOrNull() ?: 0.0
                val bonus = s.formBonusLkr.toDoubleOrNull() ?: 0.0
                val total = base + bonus
                if (total <= 0) {
                    _uiState.update { it.copy(errorMessage = "Enter a valid salary amount") }; return
                }
                IncomeRecord(
                    userId = uid, title = s.formTitle.ifBlank { "Monthly Salary" },
                    source = IncomeSource.SALARY, currency = CurrencyType.LKR,
                    originalAmount = total, convertedAmountLkr = total,
                    baseSalaryLkr = base, bonusLkr = bonus, note = s.formNote
                )
            }
            IncomeSource.FREELANCE -> {
                val amount = s.formFreelanceAmountLkr.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    _uiState.update { it.copy(errorMessage = "Enter a valid amount") }; return
                }
                IncomeRecord(
                    userId = uid, title = s.formTitle.ifBlank { "Freelance Project" },
                    source = IncomeSource.FREELANCE, currency = CurrencyType.LKR,
                    originalAmount = amount, convertedAmountLkr = amount,
                    clientName = s.formClientName, invoiceNumber = s.formInvoiceNumber,
                    milestoneStatus = s.formMilestoneStatus, note = s.formNote
                )
            }
            IncomeSource.ADSENSE -> {
                val usd = s.formUsdAmount.toDoubleOrNull()
                val rate = s.formExchangeRate.toDoubleOrNull() ?: 305.0
                if (usd == null || usd <= 0) {
                    _uiState.update { it.copy(errorMessage = "Enter a valid USD amount") }; return
                }
                val lkr = BigDecimal(usd).multiply(BigDecimal(rate))
                    .setScale(2, RoundingMode.HALF_UP).toDouble()
                IncomeRecord(
                    userId = uid, title = s.formTitle.ifBlank { "AdSense Earnings" },
                    source = IncomeSource.ADSENSE, currency = CurrencyType.USD,
                    originalAmount = usd, exchangeRate = rate,
                    convertedAmountLkr = lkr, note = s.formNote
                )
            }
            IncomeSource.CRYPTO -> {
                // Crypto is submitted via its own sheet → submitCryptoTrade()
                _uiState.update { it.copy(showAddIncomeSheet = false, showCryptoSheet = true) }
                return
            }
        }
        viewModelScope.launch {
            val result = addIncomeUseCase.addIncome(record)
            if (result is Resource.Success) {
                // Bridge: also push to FinanceRepository so Dashboard/Analytics stay correct
                FinanceRepository.addIncome(
                    IncomeItem(
                        streamType = record.source.displayName,
                        title = record.title,
                        clientOrSource = record.clientName ?: "",
                        rawAmount = record.originalAmount,
                        currency = record.currency.symbol,
                        exchangeRate = record.exchangeRate,
                        invoiceStatus = when (record.milestoneStatus) {
                            MilestoneStatus.PENDING -> InvoiceStatus.INVOICED_PENDING
                            MilestoneStatus.INVOICED -> InvoiceStatus.INVOICED_PENDING
                            MilestoneStatus.RECEIVED -> InvoiceStatus.RECEIVED
                            null -> InvoiceStatus.RECEIVED
                        }
                    )
                )
                _uiState.update { it.copy(successMessage = "Income saved!", showAddIncomeSheet = false) }
            } else if (result is Resource.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }
    private fun submitCryptoTrade() {
        val s = _uiState.value
        val uid = currentUserId ?: run {
            _uiState.update { it.copy(errorMessage = "User not authenticated") }; return
        }
        val buy = s.formBuyPriceUsd.toDoubleOrNull()
        val sell = s.formSellPriceUsd.toDoubleOrNull()
        val qty = s.formQuantity.toDoubleOrNull()
        val rate = s.formCryptoExchangeRate.toDoubleOrNull() ?: 305.0
        if (s.formAssetSymbol.isBlank() || buy == null || sell == null || qty == null || qty <= 0) {
            _uiState.update { it.copy(errorMessage = "Fill in all crypto trade fields") }
            return
        }
        val trade = CryptoTradeRecord(
            userId = uid,
            assetSymbol = s.formAssetSymbol.uppercase(),
            buyPriceUsd = buy, sellPriceUsd = sell,
            quantity = qty, exchangeRateLkr = rate,
            exchange = "Binance"
        )
        viewModelScope.launch {
            val result = addIncomeUseCase.addCryptoTrade(trade)
            if (result is Resource.Success) {
                _uiState.update { it.copy(successMessage = "Crypto trade recorded!", showCryptoSheet = false) }
            } else if (result is Resource.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }
    private fun deleteIncome(incomeId: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch { incomeRepository.deleteIncome(incomeId, uid) }
    }
}