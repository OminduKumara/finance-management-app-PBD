package com.example.pbdassignment.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.core.model.DatePeriod
import com.example.pbdassignment.data.repository.DashboardRepositoryProvider
import com.example.pbdassignment.domain.repository.DashboardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing the Analytics & Visualisations state machine.
 * Observes [DashboardRepository] for reactive [FinancialSnapshot] updates filtered by [DatePeriod].
 */
class AnalyticsViewModel(
    private val repository: DashboardRepository = DashboardRepositoryProvider.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState(isLoading = true))
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        selectPeriod(DatePeriod.allTime())
    }

    fun selectPeriod(period: DatePeriod) {
        observeJob?.cancel()
        _uiState.update { it.copy(isLoading = true, selectedPeriod = period) }

        observeJob = viewModelScope.launch {
            repository.observeFinancialSnapshot(period).collect { snapshot ->
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        selectedPeriod = period,
                        snapshot = snapshot,
                        errorMessage = null
                    )
                }
            }
        }
    }
}
