package com.example.pbdassignment.feature.income.data

import com.example.pbdassignment.core.model.IncomeItem
import kotlinx.coroutines.flow.Flow

interface LocalIncomeRepository {
    fun observeIncomes(): Flow<List<IncomeItem>>
    suspend fun addIncome(income: IncomeItem)
    suspend fun updateIncome(income: IncomeItem)
    suspend fun deleteIncome(id: String)
    suspend fun clearIncomes()
}

