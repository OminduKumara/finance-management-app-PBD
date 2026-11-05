package com.example.pbdassignment.feature.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.core.model.GoalContribution
import com.example.pbdassignment.core.model.SavingsGoal
import com.example.pbdassignment.domain.goals.CalculateGoalProgress
import com.example.pbdassignment.domain.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

import com.example.pbdassignment.data.repository.GoalRepositoryImpl

/**
 * Shared repository provider ensuring consistent state across screens during a demo session.
 */
object GoalRepositoryProvider {
    var instance: GoalRepository = GoalRepositoryImpl()
}

/**
 * Clean and straightforward ViewModel managing savings goals and contribution operations.
 */
class GoalViewModel(
    private val repository: GoalRepository = GoalRepositoryProvider.instance,
    private val calculateGoalProgress: CalculateGoalProgress = CalculateGoalProgress()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalUiState(isLoading = true))
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        observeAllGoals()
    }

    private fun observeAllGoals() {
        viewModelScope.launch {
            repository.observeGoals().collect { goalList ->
                _uiState.update { current ->
                    val updatedSelected = current.selectedGoal?.let { selected ->
                        goalList.find { it.id == selected.id }
                    } ?: goalList.firstOrNull()

                    val progress = updatedSelected?.let {
                        calculateGoalProgress(
                            targetAmountLkr = it.targetAmountLkr,
                            currentContributionTotalLkr = it.totalSavedLkr,
                            deadlineEpochMs = it.deadlineEpochMs
                        )
                    }

                    current.copy(
                        isLoading = false,
                        goals = goalList,
                        selectedGoal = updatedSelected,
                        selectedGoalProgress = progress
                    )
                }
            }
        }
    }

    fun selectGoal(goalId: String) {
        val found = _uiState.value.goals.find { it.id == goalId }
        val progress = found?.let {
            calculateGoalProgress(
                targetAmountLkr = it.targetAmountLkr,
                currentContributionTotalLkr = it.totalSavedLkr,
                deadlineEpochMs = it.deadlineEpochMs
            )
        }
        _uiState.update { it.copy(selectedGoal = found, selectedGoalProgress = progress) }
    }

    fun addContribution(goalId: String, amount: Double, note: String = "") {
        if (amount <= 0) {
            _uiState.update { it.copy(errorMessage = "Deposit amount must be greater than zero") }
            return
        }

        viewModelScope.launch {
            val contribution = GoalContribution(
                goalId = goalId,
                amountLkr = amount,
                note = note.ifBlank { "Savings contribution" }
            )

            val result = repository.addContribution(contribution)
            result.onSuccess {
                _uiState.update { it.copy(userActionSuccess = "Added LKR %,.2f to goal!".format(amount), errorMessage = null) }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.localizedMessage ?: "Failed to add contribution") }
            }
        }
    }

    fun createGoal(
        name: String,
        targetAmount: Double,
        months: Int,
        initialDeposit: Double = 0.0,
        note: String = "",
        onSuccess: () -> Unit
    ) {
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Goal name cannot be empty") }
            return
        }
        if (targetAmount <= 0) {
            _uiState.update { it.copy(errorMessage = "Target amount must be greater than zero") }
            return
        }
        val validMonths = months.coerceAtLeast(1)

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val deadlineMs = now + (validMonths.toLong() * 30L * 24 * 3600 * 1000)
            val newGoalId = UUID.randomUUID().toString()

            val contributions = if (initialDeposit > 0) {
                listOf(
                    GoalContribution(
                        goalId = newGoalId,
                        amountLkr = initialDeposit,
                        note = "Initial seed deposit"
                    )
                )
            } else {
                emptyList()
            }

            val newGoal = SavingsGoal(
                id = newGoalId,
                name = name.trim(),
                targetAmountLkr = targetAmount,
                deadlineEpochMs = deadlineMs,
                notes = note.trim(),
                contributions = contributions
            )

            repository.createGoal(newGoal).onSuccess {
                _uiState.update { it.copy(userActionSuccess = "Created goal: ${newGoal.name}", errorMessage = null) }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.localizedMessage ?: "Failed to create goal") }
            }
        }
    }

    fun deleteGoal(goalId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteGoal(goalId).onSuccess {
                _uiState.update { it.copy(userActionSuccess = "Goal deleted", errorMessage = null) }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.localizedMessage ?: "Failed to delete goal") }
            }
        }
    }

    fun deleteContribution(goalId: String, contributionId: String) {
        viewModelScope.launch {
            repository.deleteContribution(goalId, contributionId).onSuccess {
                _uiState.update { it.copy(userActionSuccess = "Deposit removed", errorMessage = null) }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.localizedMessage ?: "Failed to remove deposit") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, userActionSuccess = null) }
    }
}
