package com.example.pbd.data.repository

import android.util.Log
import com.example.pbd.core.firebase.FirebaseManager
import com.example.pbd.core.model.GoalContribution
import com.example.pbd.core.model.SavingsGoal
import com.example.pbd.domain.repository.GoalRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Production implementation of [GoalRepository] scoped to the authenticated user.
 * Syncs with Cloud Firestore path:
 * - users/{uid}/goals/{goalId}
 * - users/{uid}/goals/{goalId}/contributions/{contributionId}
 *
 * Maintains an in-memory cache to guarantee instantaneous UI updates and offline resilience.
 */
class GoalRepositoryImpl(
    initialGoals: List<SavingsGoal> = listOf(SavingsGoal.createDefaultMacBookGoal()),
    private val getFirestore: () -> FirebaseFirestore? = {
        try {
            FirebaseFirestore.getInstance()
        } catch (_: Exception) {
            null
        }
    }
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

        syncGoalToFirestore(goal)
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

        if (found) {
            syncGoalToFirestore(goal)
            return Result.success(Unit)
        }
        return Result.failure(NoSuchElementException("Goal not found: ${goal.id}"))
    }

    override suspend fun deleteGoal(goalId: String): Result<Unit> {
        _goals.update { list -> list.filterNot { it.id == goalId } }
        deleteGoalFromFirestore(goalId)
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

        if (targetFound) {
            syncContributionToFirestore(contribution)
            return Result.success(Unit)
        }
        return Result.failure(NoSuchElementException("Goal not found for contribution: ${contribution.goalId}"))
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
        deleteContributionFromFirestore(goalId, contributionId)
        return Result.success(Unit)
    }

    private fun syncGoalToFirestore(goal: SavingsGoal) {
        try {
            val firestore = getFirestore() ?: return
            val uid = FirebaseManager.getCurrentUserId()
            val docData = hashMapOf(
                "id" to goal.id,
                "userId" to uid,
                "name" to goal.name,
                "targetAmountLkr" to goal.targetAmountLkr,
                "totalSavedLkr" to goal.totalSavedLkr,
                "deadlineEpochMs" to goal.deadlineEpochMs,
                "createdAtEpochMs" to goal.createdAtEpochMs,
                "status" to goal.status.name,
                "notes" to goal.notes
            )
            firestore.collection("users").document(uid)
                .collection("goals").document(goal.id)
                .set(docData)
        } catch (e: Exception) {
            Log.w("GoalRepositoryImpl", "Firestore sync goal failed: ${e.message}")
        }
    }

    private fun syncContributionToFirestore(contribution: GoalContribution) {
        try {
            val firestore = getFirestore() ?: return
            val uid = FirebaseManager.getCurrentUserId()
            val docData = hashMapOf(
                "id" to contribution.id,
                "goalId" to contribution.goalId,
                "userId" to uid,
                "amountLkr" to contribution.amountLkr,
                "dateEpochMs" to contribution.dateEpochMs,
                "note" to contribution.note
            )
            firestore.collection("users").document(uid)
                .collection("goals").document(contribution.goalId)
                .collection("contributions").document(contribution.id)
                .set(docData)
        } catch (e: Exception) {
            Log.w("GoalRepositoryImpl", "Firestore sync contribution failed: ${e.message}")
        }
    }

    private fun deleteGoalFromFirestore(goalId: String) {
        try {
            val firestore = getFirestore() ?: return
            val uid = FirebaseManager.getCurrentUserId()
            firestore.collection("users").document(uid)
                .collection("goals").document(goalId)
                .delete()
        } catch (e: Exception) {
            Log.w("GoalRepositoryImpl", "Firestore delete goal failed: ${e.message}")
        }
    }

    private fun deleteContributionFromFirestore(goalId: String, contributionId: String) {
        try {
            val firestore = getFirestore() ?: return
            val uid = FirebaseManager.getCurrentUserId()
            firestore.collection("users").document(uid)
                .collection("goals").document(goalId)
                .collection("contributions").document(contributionId)
                .delete()
        } catch (e: Exception) {
            Log.w("GoalRepositoryImpl", "Firestore delete contribution failed: ${e.message}")
        }
    }
}
