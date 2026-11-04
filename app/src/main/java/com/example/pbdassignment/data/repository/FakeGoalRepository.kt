package com.example.pbdassignment.data.repository

import com.example.pbdassignment.core.model.GoalContribution
import com.example.pbdassignment.core.model.SavingsGoal
import com.example.pbdassignment.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * In-memory thread-safe fake implementation of [GoalRepository].
 *
 * Pre-seeded with Kavindu Silva's authentic MacBook Pro M4 target.
 * Enables complete independent UI development and ViewModel testing.
 */
class FakeGoalRepository(
    initialGoals: List<SavingsGoal> = listOf(SavingsGoal.createDefaultMacBookGoal())
) : GoalRepository {

    private val _goals = MutableStateFlow(initialGoals)

    override fun observeGoals(): Flow<List<SavingsGoal>> {
        return _goals.asStateFlow()
    }

    override fun observeGoalById(goalId: String): Flow<SavingsGoal?> {
        return _goals.map { list -> list.find { it.id == goalId } }
    }

    override fun observeContributions(goalId: String): Flow<List<GoalContribution>> {
        return _goals.map { list ->
            list.find { it.id == goalId }?.contributions ?: emptyList()
        }
    }

    override suspend fun createGoal(goal: SavingsGoal): Result<Unit> {
        if (goal.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Goal title cannot be blank"))
        }
        if (goal.targetAmountLkr <= 0.0) {
            return Result.failure(IllegalArgumentException("Target amount must be strictly positive"))
        }

        _goals.update { current ->
            if (current.any { it.id == goal.id }) {
                current.map { if (it.id == goal.id) goal else it }
            } else {
                listOf(goal) + current
            }
        }
        return Result.success(Unit)
    }

    override suspend fun updateGoal(goal: SavingsGoal): Result<Unit> {
        if (goal.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Goal title cannot be blank"))
        }
        if (goal.targetAmountLkr <= 0.0) {
            return Result.failure(IllegalArgumentException("Target amount must be strictly positive"))
        }

        var found = false
        _goals.update { list ->
            list.map {
                if (it.id == goal.id) {
                    found = true
                    goal
                } else it
            }
        }
        return if (found) Result.success(Unit) else Result.failure(NoSuchElementException("Goal not found: ${goal.id}"))
    }

    override suspend fun deleteGoal(goalId: String): Result<Unit> {
        _goals.update { list -> list.filterNot { it.id == goalId } }
        return Result.success(Unit)
    }

    override suspend fun addContribution(contribution: GoalContribution): Result<Unit> {
        if (contribution.amountLkr <= 0.0) {
            return Result.failure(IllegalArgumentException("Contribution amount must be strictly positive"))
        }

        var targetFound = false
        _goals.update { list ->
            list.map { goal ->
                if (goal.id == contribution.goalId) {
                    targetFound = true
                    val updatedContributions = listOf(contribution) + goal.contributions
                    goal.copy(contributions = updatedContributions)
                } else {
                    goal
                }
            }
        }

        return if (targetFound) {
            Result.success(Unit)
        } else {
            Result.failure(NoSuchElementException("Goal not found for contribution: ${contribution.goalId}"))
        }
    }

    override suspend fun deleteContribution(goalId: String, contributionId: String): Result<Unit> {
        _goals.update { list ->
            list.map { goal ->
                if (goal.id == goalId) {
                    val updatedContributions = goal.contributions.filterNot { it.id == contributionId }
                    goal.copy(contributions = updatedContributions)
                } else {
                    goal
                }
            }
        }
        return Result.success(Unit)
    }

    /**
     * Resets the repository state back to empty or default for clean test teardowns.
     */
    fun reset(goals: List<SavingsGoal> = listOf(SavingsGoal.createDefaultMacBookGoal())) {
        _goals.value = goals
    }
}
