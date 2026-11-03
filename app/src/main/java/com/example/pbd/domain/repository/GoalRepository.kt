package com.example.pbd.domain.repository

import com.example.pbd.core.model.GoalContribution
import com.example.pbd.core.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface governing multi-goal tracking and contribution persistence.
 * Implements clean Result-based error propagation.
 */
interface GoalRepository {

    /**
     * Continuous reactive stream of all savings goals owned by the authenticated user.
     */
    fun observeGoals(): Flow<List<SavingsGoal>>

    /**
     * Observes a single goal by its identifier.
     */
    fun observeGoalById(goalId: String): Flow<SavingsGoal?>

    /**
     * Observes contribution deposits for a specific goal.
     */
    fun observeContributions(goalId: String): Flow<List<GoalContribution>>

    /**
     * Creates a new savings goal.
     */
    suspend fun createGoal(goal: SavingsGoal): Result<Unit>

    /**
     * Updates an existing savings goal.
     */
    suspend fun updateGoal(goal: SavingsGoal): Result<Unit>

    /**
     * Deletes a savings goal and its associated contributions.
     */
    suspend fun deleteGoal(goalId: String): Result<Unit>

    /**
     * Records a contribution deposit towards a savings goal.
     */
    suspend fun addContribution(contribution: GoalContribution): Result<Unit>

    /**
     * Deletes an individual contribution record.
     */
    suspend fun deleteContribution(goalId: String, contributionId: String): Result<Unit>
}
