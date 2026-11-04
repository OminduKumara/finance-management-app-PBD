package com.example.pbdassignment.data.repository

import com.example.pbdassignment.core.model.GoalContribution
import com.example.pbdassignment.core.model.SavingsGoal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeGoalRepositoryTest {

    private lateinit var repository: FakeGoalRepository

    @Before
    fun setup() {
        repository = FakeGoalRepository()
    }

    @Test
    fun `initial state contains pre-seeded default MacBook goal`() = runBlocking {
        val goals = repository.observeGoals().first()
        assertEquals(1, goals.size)
        assertEquals("macbook_pro_m4", goals[0].id)
        assertEquals(490000.0, goals[0].targetAmountLkr, 0.01)
        assertEquals(11200.0, goals[0].totalSavedLkr, 0.01)
    }

    @Test
    fun `creating a valid goal successfully persists in stream`() = runBlocking {
        val newGoal = SavingsGoal(
            id = "emergency_fund",
            name = "Emergency Buffer (3 Months)",
            targetAmountLkr = 200000.0,
            deadlineEpochMs = System.currentTimeMillis() + (180L * 24 * 3600 * 1000)
        )

        val result = repository.createGoal(newGoal)
        assertTrue(result.isSuccess)

        val goals = repository.observeGoals().first()
        assertEquals(2, goals.size)
        assertTrue(goals.any { it.id == "emergency_fund" })
    }

    @Test
    fun `creating a goal with blank title fails with validation exception`() = runBlocking {
        val invalidGoal = SavingsGoal(
            id = "bad_goal",
            name = "   ",
            targetAmountLkr = 50000.0,
            deadlineEpochMs = System.currentTimeMillis() + 10000
        )

        val result = repository.createGoal(invalidGoal)
        assertTrue(result.isFailure)
    }

    @Test
    fun `adding a contribution increments goal saved amount and updates contributions flow`() = runBlocking {
        val contribution = GoalContribution(
            id = "contrib_salary_sweep",
            goalId = "macbook_pro_m4",
            amountLkr = 25000.0,
            note = "October freelance surplus allocation"
        )

        val result = repository.addContribution(contribution)
        assertTrue(result.isSuccess)

        val goal = repository.observeGoalById("macbook_pro_m4").first()
        assertNotNull(goal)
        // 11,200 initial + 25,000 = 36,200
        assertEquals(36200.0, goal!!.totalSavedLkr, 0.01)

        val contributions = repository.observeContributions("macbook_pro_m4").first()
        assertEquals(2, contributions.size)
        assertTrue(contributions.any { it.id == "contrib_salary_sweep" })
    }

    @Test
    fun `adding a negative contribution is rejected`() = runBlocking {
        val invalidContribution = GoalContribution(
            id = "bad_contrib",
            goalId = "macbook_pro_m4",
            amountLkr = -5000.0
        )

        val result = repository.addContribution(invalidContribution)
        assertTrue(result.isFailure)
    }

    @Test
    fun `deleting a contribution updates total saved correctly`() = runBlocking {
        repository.deleteContribution("macbook_pro_m4", "contrib_initial_seed")
        val goal = repository.observeGoalById("macbook_pro_m4").first()
        assertNotNull(goal)
        assertEquals(0.0, goal!!.totalSavedLkr, 0.01)
    }

    @Test
    fun `deleting a goal removes it from repository stream`() = runBlocking {
        val result = repository.deleteGoal("macbook_pro_m4")
        assertTrue(result.isSuccess)

        val goals = repository.observeGoals().first()
        assertTrue(goals.isEmpty())

        val deletedGoal = repository.observeGoalById("macbook_pro_m4").first()
        assertNull(deletedGoal)
    }
}
