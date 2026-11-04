package com.example.pbdassignment.feature.recurring.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import com.example.pbdassignment.feature.recurring.domain.RecurringExpense
import com.example.pbdassignment.feature.recurring.domain.RecurringExpenseRepository
class RoomRecurringExpenseRepository(
    private val dao: RecurringExpenseDao
) : RecurringExpenseRepository {

    override fun observeRecurringExpenses(): Flow<List<RecurringExpense>> {
        return dao.observeRecurringExpenses().map { expenses ->
            expenses.map { it.toDomain() }
        }
    }

    override suspend fun addRecurringExpense(expense: RecurringExpense) {
        dao.insertRecurringExpense(expense.toEntity())
    }

    override suspend fun updateRecurringExpense(expense: RecurringExpense) {
        dao.updateRecurringExpense(expense.toEntity())
    }

    override suspend fun deleteRecurringExpense(id: String) {
        dao.deleteRecurringExpense(id)
    }

    override suspend fun getActiveRecurringExpenses(): List<RecurringExpense> {
        return dao.getActiveRecurringExpenses().map { it.toDomain() }
    }

    override suspend fun getRecurringExpenseById(id: String): RecurringExpense? {
        return dao.getRecurringExpenseById(id)?.toDomain()
    }

}