package com.example.pbdassignment.feature.income.data

import com.example.pbdassignment.core.common.Resource
import com.example.pbdassignment.feature.income.domain.CryptoTradeRecord
import com.example.pbdassignment.feature.income.domain.IncomeRecord
import com.example.pbdassignment.feature.income.domain.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class IncomeRepositoryImpl : IncomeRepository {
    override fun getIncomes(userId: String): Flow<List<IncomeRecord>> {
        return flowOf(emptyList())
    }

    override suspend fun addIncome(income: IncomeRecord): Resource<Unit> {
        return Resource.Success(Unit)
    }

    override suspend fun deleteIncome(
        incomeId: String,
        userId: String
    ): Resource<Unit> {
        return Resource.Success(Unit)
    }

    override fun getCryptoTrades(
        userId: String
    ): Flow<List<CryptoTradeRecord>> {
        return flowOf(emptyList())
    }

    override suspend fun addCryptoTrade(
        trade: CryptoTradeRecord
    ): Resource<Unit> {
        return Resource.Success(Unit)
    }
}