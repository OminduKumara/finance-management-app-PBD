package com.example.pbdassignment.feature.recurring.domain

import kotlinx.coroutines.flow.Flow

interface RecurringExpenseRepository {

    fun observeRecurringExpenses(): Flow<List<RecurringExpense>>

    suspend fun addRecurringExpense(expense: RecurringExpense)

    suspend fun updateRecurringExpense(expense: RecurringExpense)

    suspend fun deleteRecurringExpense(id: String)

    suspend fun getActiveRecurringExpenses(): List<RecurringExpense>

    suspend fun getRecurringExpenseById(id: String): RecurringExpense?
}