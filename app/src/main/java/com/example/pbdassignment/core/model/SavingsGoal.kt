package com.example.pbdassignment.core.model

import java.util.UUID

enum class GoalStatus(val label: String) {
    ACTIVE("Active"),
    ACHIEVED("Achieved"),
    OVERDUE("Overdue"),
    PAUSED("Paused")
}

/**
 * Represents an individual contribution/deposit into a savings goal.
 */
data class GoalContribution(
    val id: String = UUID.randomUUID().toString(),
    val goalId: String,
    val amountLkr: Double,
    val dateEpochMs: Long = System.currentTimeMillis(),
    val note: String = ""
)

/**
 * SavingsGoal model supporting multi-goal tracking, deadlines,
 * and user-specific contribution histories.
 */
data class SavingsGoal(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val targetAmountLkr: Double,
    val deadlineEpochMs: Long,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val status: GoalStatus = GoalStatus.ACTIVE,
    val notes: String = "",
    val contributions: List<GoalContribution> = emptyList()
) {
    val totalSavedLkr: Double
        get() = contributions.sumOf { it.amountLkr }

    val remainingAmountLkr: Double
        get() = (targetAmountLkr - totalSavedLkr).coerceAtLeast(0.0)

    val progressFraction: Float
        get() = if (targetAmountLkr > 0) (totalSavedLkr / targetAmountLkr).toFloat().coerceIn(0f, 1f) else 0f

    val isAchieved: Boolean
        get() = totalSavedLkr >= targetAmountLkr

    fun isOverdue(currentEpochMs: Long = System.currentTimeMillis()): Boolean {
        return currentEpochMs > deadlineEpochMs && !isAchieved
    }

    /**
     * Converts to GoalItem for backwards compatibility with earlier components.
     */
    fun toGoalItem(targetMonths: Int = 12): GoalItem {
        return GoalItem(
            id = id,
            title = name,
            targetAmount = targetAmountLkr,
            currentSaved = totalSavedLkr,
            targetMonths = targetMonths
        )
    }

    companion object {
        /**
         * Generates the authentic pre-seeded MacBook Pro M4 goal for Kavindu Silva.
         */
        fun createDefaultMacBookGoal(): SavingsGoal {
            val now = System.currentTimeMillis()
            val twelveMonthsMs = 365L * 24 * 3600 * 1000
            val goalId = "macbook_pro_m4"
            return SavingsGoal(
                id = goalId,
                name = "MacBook Pro M4 (14-inch)",
                targetAmountLkr = 490000.0,
                deadlineEpochMs = now + twelveMonthsMs,
                createdAtEpochMs = now - (30L * 24 * 3600 * 1000),
                status = GoalStatus.ACTIVE,
                notes = "Required for freelance engineering & mobile app builds",
                contributions = listOf(
                    GoalContribution(
                        id = "contrib_initial_seed",
                        goalId = goalId,
                        amountLkr = 11200.0,
                        dateEpochMs = now - (15L * 24 * 3600 * 1000),
                        note = "Initial allocation from September salary surplus"
                    )
                )
            )
        }
    }
}
