package com.example.pbdassignment.feature.income.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.core.common.Resource
import com.example.pbdassignment.feature.authentication.domain.AuthRepository
import com.example.pbdassignment.feature.income.domain.AddIncome
import com.example.pbdassignment.feature.income.domain.CalculateRealisedPnL
import com.example.pbdassignment.feature.income.domain.ConvertToLkr
import com.example.pbdassignment.feature.income.domain.GetAggregatedIncome
import com.example.pbdassignment.feature.income.domain.IncomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        observeUserAndLoadData()
    }

    private fun observeUserAndLoadData() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                currentUserId = user?.uid
                if (user != null) {
                    observeIncomeData(user.uid)
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun observeIncomeData(userId: String) {
        viewModelScope.launch {
            combine(
                incomeRepository.getIncomes(userId),
                incomeRepository.getCryptoTrades(userId)
            ) { incomes, trades ->
                Pair(incomes, trades)
            }.collect { (incomes, trades) ->
                val aggregated = aggregateUseCase(
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
                        totalReceivedLkr = aggregated.totalReceivedLkr,
                        salarySumLkr = aggregated.salarySumLkr,
                        freelanceSumLkr = aggregated.freelanceSumLkr,
                        adsenseSumLkr = aggregated.adsenseSumLkr,
                        cryptoPnLLkr = aggregated.cryptoPnLSumLkr,
                        pendingFreelanceLkr = aggregated.pendingFreelanceLkr,
                        pendingFreelanceCount = aggregated.pendingFreelanceCount
                    )
                }
            }
        }
    }

    fun onEvent(event: IncomeUiEvent) {
        when (event) {
            is IncomeUiEvent.AddIncome -> addIncome(event.income)
            is IncomeUiEvent.AddCryptoTrade -> addCryptoTrade(event.trade)
            is IncomeUiEvent.DeleteIncome -> deleteIncome(event.incomeId)
            is IncomeUiEvent.SelectMonth -> {
                _uiState.update { it.copy(selectedMonth = event.month, selectedYear = event.year) }
                currentUserId?.let { observeIncomeData(it) }
            }
            is IncomeUiEvent.ClearMessage -> {
                _uiState.update { it.copy(errorMessage = null, successMessage = null) }
            }
        }
    }

    private fun addIncome(income: com.example.pbdassignment.feature.income.domain.IncomeRecord) {
        val uid = currentUserId ?: run {
            _uiState.update { it.copy(errorMessage = "User not authenticated") }
            return
        }
        viewModelScope.launch {
            val result = addIncomeUseCase.addIncome(income.copy(userId = uid))
            when (result) {
                is Resource.Success -> _uiState.update { it.copy(successMessage = "Income added successfully") }
                is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                else -> Unit
            }
        }
    }

    private fun addCryptoTrade(trade: com.example.pbdassignment.feature.income.domain.CryptoTradeRecord) {
        val uid = currentUserId ?: run {
            _uiState.update { it.copy(errorMessage = "User not authenticated") }
            return
        }
        viewModelScope.launch {
            val result = addIncomeUseCase.addCryptoTrade(trade.copy(userId = uid))
            when (result) {
                is Resource.Success -> _uiState.update { it.copy(successMessage = "Crypto trade recorded") }
                is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                else -> Unit
            }
        }
    }

    private fun deleteIncome(incomeId: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            incomeRepository.deleteIncome(incomeId, uid)
        }
    }
}