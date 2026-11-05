package com.example.pbdassignment.feature.recurring.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringExpenseDao {

    @Query("SELECT * FROM recurring_expenses ORDER BY startDateMillis DESC")
    fun observeRecurringExpenses(): Flow<List<RecurringExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringExpense(expense: RecurringExpenseEntity)

    @Update
    suspend fun updateRecurringExpense(expense: RecurringExpenseEntity)

    @Query("DELETE FROM recurring_expenses WHERE id = :expenseId")
    suspend fun deleteRecurringExpense(expenseId: String)

    @Query("SELECT * FROM recurring_expenses WHERE isActive = 1")
    suspend fun getActiveRecurringExpenses(): List<RecurringExpenseEntity>

    @Query("SELECT * FROM recurring_expenses WHERE id = :expenseId LIMIT 1")
    suspend fun getRecurringExpenseById(expenseId: String): RecurringExpenseEntity?
}