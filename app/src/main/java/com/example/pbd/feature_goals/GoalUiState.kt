package com.example.pbd.feature_goals

import com.example.pbd.core.model.SavingsGoal
import com.example.pbd.domain.goals.GoalProgressResult

/**
 * Single immutable UI state representing the savings goals screens.
 * Simple, concise, and eliminates complex boilerplate.
 */
data class GoalUiState(
    val isLoading: Boolean = false,
    val goals: List<SavingsGoal> = emptyList(),
    val selectedGoal: SavingsGoal? = null,
    val selectedGoalProgress: GoalProgressResult? = null,
    val errorMessage: String? = null,
    val userActionSuccess: String? = null
)
