package com.example.pbdassignment.feature.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.core.data.FinanceRepository
import com.example.pbdassignment.core.model.IncomeItem
import com.example.pbdassignment.core.model.InvoiceStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class IncomeUiState(
    val items: List<IncomeItem> = emptyList(),
    val totalRealizedLkr: Double = 0.0,
    val pendingInvoicesLkr: Double = 0.0,
    val overdueInvoicesCount: Int = 0,
    val selectedFilter: String? = null
)

class IncomeViewModel : ViewModel() {

    private val _selectedFilter = MutableStateFlow<String?>(null)

    val uiState: StateFlow<IncomeUiState> = combine(
        FinanceRepository.incomes,
        _selectedFilter
    ) { incomes, filter ->
        val filtered = if (filter == null) incomes else incomes.filter { it.streamType.equals(filter, ignoreCase = true) }
        val realized = incomes.filter { it.invoiceStatus == InvoiceStatus.RECEIVED || it.invoiceStatus == InvoiceStatus.SETTLED }
            .sumOf { it.lkrAmount }
        val pending = incomes.filter { it.invoiceStatus == InvoiceStatus.INVOICED_PENDING || it.invoiceStatus == InvoiceStatus.OVERDUE }
            .sumOf { it.lkrAmount }
        val overdueCount = incomes.count { it.invoiceStatus == InvoiceStatus.OVERDUE }

        IncomeUiState(
            items = filtered,
            totalRealizedLkr = realized,
            pendingInvoicesLkr = pending,
            overdueInvoicesCount = overdueCount,
            selectedFilter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncomeUiState()
    )

    fun setFilter(streamType: String?) {
        _selectedFilter.value = streamType
    }

    fun addIncome(
        streamType: String,
        title: String,
        source: String,
        rawAmount: Double,
        currency: String = "LKR",
        exchangeRate: Double = 1.0,
        isPendingInvoice: Boolean = false
    ) {
        val status = if (isPendingInvoice) InvoiceStatus.INVOICED_PENDING else InvoiceStatus.RECEIVED
        val newItem = IncomeItem(
            streamType = streamType.ifBlank { "Income" },
            title = title,
            clientOrSource = source,
            rawAmount = rawAmount,
            currency = currency,
            exchangeRate = exchangeRate,
            invoiceStatus = status
        )
        FinanceRepository.addIncome(newItem)
    }

    fun markInvoiceSettled(id: String, sweepPercentage: Int = 0) {
        val income = FinanceRepository.incomes.value.find { it.id == id } ?: return
        FinanceRepository.updateInvoiceStatus(id, InvoiceStatus.SETTLED)
        if (sweepPercentage > 0) {
            val sweepAmount = (income.lkrAmount * sweepPercentage) / 100.0
            FinanceRepository.allocateToGoal(sweepAmount)
        }
    }
}
