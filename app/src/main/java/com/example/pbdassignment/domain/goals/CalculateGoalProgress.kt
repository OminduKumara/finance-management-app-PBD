package com.example.pbdassignment.domain.goals

import kotlin.math.ceil
import kotlin.math.round

/**
 * Result model emitted by [CalculateGoalProgress].
 */
data class GoalProgressResult(
    val targetAmountLkr: Double,
    val currentSavedLkr: Double,
    val remainingAmountLkr: Double,
    val progressFraction: Float,
    val remainingMonths: Int,
    val requiredMonthlyContributionLkr: Double,
    val isCompleted: Boolean,
    val isOverdue: Boolean,
    val isValid: Boolean = true,
    val validationError: String? = null
)

/**
 * Pure Kotlin domain component for calculating goal progress, remaining runway,
 * and monthly contribution velocity.
 *
 * Fully testable with injected/explicit timestamps.
 */
class CalculateGoalProgress {

    operator fun invoke(
        targetAmountLkr: Double,
        currentContributionTotalLkr: Double,
        deadlineEpochMs: Long,
        currentEpochMs: Long = System.currentTimeMillis()
    ): GoalProgressResult {

        // 1. Validation checks
        if (targetAmountLkr <= 0.0) {
            return GoalProgressResult(
                targetAmountLkr = targetAmountLkr,
                currentSavedLkr = currentContributionTotalLkr,
                remainingAmountLkr = 0.0,
                progressFraction = 0f,
                remainingMonths = 0,
                requiredMonthlyContributionLkr = 0.0,
                isCompleted = false,
                isOverdue = false,
                isValid = false,
                validationError = "Target amount must be strictly positive"
            )
        }

        val sanitizedSaved = currentContributionTotalLkr.coerceAtLeast(0.0)
        val remainingAmount = (targetAmountLkr - sanitizedSaved).coerceAtLeast(0.0)
        val isCompleted = sanitizedSaved >= targetAmountLkr
        val isOverdue = (currentEpochMs > deadlineEpochMs) && !isCompleted

        // Clamp visual progress to [0f, 1f] while preserving actual saved value
        val progressFraction = (sanitizedSaved / targetAmountLkr).toFloat().coerceIn(0f, 1f)

        // 2. Month runway calculation
        val msDifference = deadlineEpochMs - currentEpochMs
        val remainingDays = ceil(msDifference / (24.0 * 3600.0 * 1000.0)).toInt()

        val remainingMonths = when {
            isCompleted -> 0
            isOverdue -> 0
            remainingDays <= 0 -> 1
            else -> ceil(remainingDays / 30.4375).toInt().coerceAtLeast(1)
        }

        // 3. Required Monthly Contribution
        val requiredMonthlyContribution = when {
            isCompleted -> 0.0
            isOverdue -> 0.0 // Deadline passed, cannot compute future monthly schedule
            remainingMonths > 0 -> roundToTwoDecimals(remainingAmount / remainingMonths)
            else -> roundToTwoDecimals(remainingAmount)
        }

        return GoalProgressResult(
            targetAmountLkr = roundToTwoDecimals(targetAmountLkr),
            currentSavedLkr = roundToTwoDecimals(sanitizedSaved),
            remainingAmountLkr = roundToTwoDecimals(remainingAmount),
            progressFraction = progressFraction,
            remainingMonths = remainingMonths,
            requiredMonthlyContributionLkr = requiredMonthlyContribution,
            isCompleted = isCompleted,
            isOverdue = isOverdue,
            isValid = true
        )
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100.0) / 100.0
    }
}
