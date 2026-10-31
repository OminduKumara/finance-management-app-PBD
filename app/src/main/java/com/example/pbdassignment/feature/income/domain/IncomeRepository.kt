package com.example.pbdassignment.feature.income.domain

import com.example.pbdassignment.core.common.Resource
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {

    fun getIncomes(userId: String): Flow<List<IncomeRecord>>
    suspend fun addIncome(income: IncomeRecord): Resource<Unit>
    suspend fun deleteIncome(incomeId: String, userId: String): Resource<Unit>

    fun getCryptoTrades(userId: String): Flow<List<CryptoTradeRecord>>
    suspend fun addCryptoTrade(trade: CryptoTradeRecord): Resource<Unit>

}

