package com.example.pbdassignment.feature.income.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.feature.authentication.domain.AuthRepository
import com.example.pbdassignment.feature.income.domain.IncomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IncomeViewModel(
    private val incomeRepository: IncomeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

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
            incomeRepository.getIncomes(userId).collect { incomes ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        incomeList = incomes
                    )
                }
            }
        }
    }
}