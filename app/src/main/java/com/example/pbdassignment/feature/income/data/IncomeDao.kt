package com.example.pbdassignment.feature.income.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Query("SELECT * FROM incomes ORDER BY date DESC")
    fun observeIncomes(): Flow<List<IncomeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncomes(incomes: List<IncomeEntity>)

    @Update
    suspend fun updateIncome(income: IncomeEntity)

    @Query("DELETE FROM incomes WHERE id = :incomeId")
    suspend fun deleteIncome(incomeId: String)

    @Query("DELETE FROM incomes")
    suspend fun clearIncomes()
}

