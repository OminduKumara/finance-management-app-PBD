package com.example.pbdassignment.domain.goals

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculateGoalProgressTest {

    private lateinit var calculateProgress: CalculateGoalProgress
    private val baseTimestamp = 1730190000000L // Fixed reference timestamp (Day 0)
    private val oneDayMs = 24L * 3600 * 1000
    private val oneMonthMs = 30L * oneDayMs

    @Before
    fun setup() {
        calculateProgress = CalculateGoalProgress()
    }

    @Test
    fun `newly created goal with zero saved calculates full deficit and positive monthly pace`() {
        val target = 490000.0
        val saved = 0.0
        val deadline = baseTimestamp + (365L * oneDayMs) // 12 months ahead

        val result = calculateProgress(
            targetAmountLkr = target,
            currentContributionTotalLkr = saved,
            deadlineEpochMs = deadline,
            currentEpochMs = baseTimestamp
        )

        assertTrue(result.isValid)
        assertEquals(490000.0, result.targetAmountLkr, 0.01)
        assertEquals(0.0, result.currentSavedLkr, 0.01)
        assertEquals(490000.0, result.remainingAmountLkr, 0.01)
        assertEquals(0f, result.progressFraction, 0.001f)
        assertFalse(result.isCompleted)
        assertFalse(result.isOverdue)
        assertEquals(12, result.remainingMonths)
        // 490,000 / 12 = ~40,833.33
        assertEquals(40833.33, result.requiredMonthlyContributionLkr, 0.01)
    }

    @Test
    fun `partially completed goal computes accurate remaining amount and velocity`() {
        val target = 490000.0
        val saved = 11200.0 // Kavindu initial seed
        val deadline = baseTimestamp + (365L * oneDayMs)

        val result = calculateProgress(
            targetAmountLkr = target,
            currentContributionTotalLkr = saved,
            deadlineEpochMs = deadline,
            currentEpochMs = baseTimestamp
        )

        val expectedRemaining = 478800.0
        assertEquals(expectedRemaining, result.remainingAmountLkr, 0.01)
        // 11200 / 490000 = ~0.02285
        assertEquals(0.0229f, result.progressFraction, 0.001f)
        assertFalse(result.isCompleted)
        // 478800 / 12 = 39,900
        assertEquals(39900.0, result.requiredMonthlyContributionLkr, 0.01)
    }

    @Test
    fun `completed goal has zero remaining deficit and zero required monthly contribution`() {
        val target = 490000.0
        val saved = 490000.0
        val deadline = baseTimestamp + (60L * oneDayMs)

        val result = calculateProgress(
            targetAmountLkr = target,
            currentContributionTotalLkr = saved,
            deadlineEpochMs = deadline,
            currentEpochMs = baseTimestamp
        )

        assertTrue(result.isCompleted)
        assertFalse(result.isOverdue)
        assertEquals(0.0, result.remainingAmountLkr, 0.01)
        assertEquals(1f, result.progressFraction, 0.001f)
        assertEquals(0.0, result.requiredMonthlyContributionLkr, 0.01)
    }

    @Test
    fun `contributions exceeding target clamp visual progress to 1 without losing actual total`() {
        val target = 490000.0
        val saved = 550000.0 // Exceeded by 60k
        val deadline = baseTimestamp + (60L * oneDayMs)

        val result = calculateProgress(
            targetAmountLkr = target,
            currentContributionTotalLkr = saved,
            deadlineEpochMs = deadline,
            currentEpochMs = baseTimestamp
        )

        assertTrue(result.isCompleted)
        assertEquals(550000.0, result.currentSavedLkr, 0.01) // Preserved
        assertEquals(0.0, result.remainingAmountLkr, 0.01)
        assertEquals(1f, result.progressFraction, 0.001f) // Clamped
        assertEquals(0.0, result.requiredMonthlyContributionLkr, 0.01)
    }

    @Test
    fun `overdue deadline marks goal as overdue and disables future monthly calculation`() {
        val target = 490000.0
        val saved = 200000.0
        val passedDeadline = baseTimestamp - (10L * oneDayMs) // 10 days in the past

        val result = calculateProgress(
            targetAmountLkr = target,
            currentContributionTotalLkr = saved,
            deadlineEpochMs = passedDeadline,
            currentEpochMs = baseTimestamp
        )

        assertFalse(result.isCompleted)
        assertTrue(result.isOverdue)
        assertEquals(290000.0, result.remainingAmountLkr, 0.01)
        assertEquals(0.0, result.requiredMonthlyContributionLkr, 0.01) // Cannot project future monthly schedule
    }

    @Test
    fun `goal completed past the deadline is not marked as overdue`() {
        val target = 490000.0
        val saved = 490000.0
        val passedDeadline = baseTimestamp - (10L * oneDayMs)

        val result = calculateProgress(
            targetAmountLkr = target,
            currentContributionTotalLkr = saved,
            deadlineEpochMs = passedDeadline,
            currentEpochMs = baseTimestamp
        )

        assertTrue(result.isCompleted)
        assertFalse(result.isOverdue) // Successfully completed, not overdue
    }

    @Test
    fun `zero or negative target is rejected with validation error`() {
        val result = calculateProgress(
            targetAmountLkr = -500.0,
            currentContributionTotalLkr = 0.0,
            deadlineEpochMs = baseTimestamp + (30L * oneDayMs),
            currentEpochMs = baseTimestamp
        )

        assertFalse(result.isValid)
        assertNotNull(result.validationError)
    }
}
