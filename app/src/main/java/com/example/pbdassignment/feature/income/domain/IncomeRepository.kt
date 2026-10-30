package com.example.pbdassignment.feature.income.domain

import com.example.pbdassignment.core.common.Resource
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {

    fun getIncomes(userId: String): Flow<List<IncomeRecord>>
    suspend fun addIncome(income: IncomeRecord): Resource<Unit>
}