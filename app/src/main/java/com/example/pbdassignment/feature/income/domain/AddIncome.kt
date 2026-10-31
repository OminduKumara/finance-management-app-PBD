package com.example.pbdassignment.feature.income.domain

import com.example.pbdassignment.core.common.Resource

class AddIncome(
    private val incomeRepository: IncomeRepository,
    private val convertToLkr: ConvertToLkr,
    private val calculatePnL: CalculateRealisedPnL
) {
    suspend fun addIncome(income: IncomeRecord): Resource<Unit> {
        if (income.title.isBlank()) return Resource.Error("Title is required")
        if (income.originalAmount <= 0) return Resource.Error("Amount must be greater than zero")
        if (income.userId.isBlank()) return Resource.Error("User not authenticated")
        // For AdSense: calculate LKR equivalent
        val finalIncome = if (income.source == IncomeSource.ADSENSE) {
            val conversion = convertToLkr(income.originalAmount, income.exchangeRate)
            if (!conversion.isValid) return Resource.Error(conversion.errorMessage ?: "Conversion failed")
            income.copy(convertedAmountLkr = conversion.convertedAmountLkr)
        } else {
            income.copy(convertedAmountLkr = income.originalAmount)
        }
        return incomeRepository.addIncome(finalIncome)
    }
    suspend fun addCryptoTrade(trade: CryptoTradeRecord): Resource<Unit> {
        if (trade.quantity <= 0) return Resource.Error("Quantity must be greater than zero")
        if (trade.assetSymbol.isBlank()) return Resource.Error("Asset symbol is required")
        val pnl = calculatePnL(
            buyPriceUsd = trade.buyPriceUsd,
            sellPriceUsd = trade.sellPriceUsd,
            quantity = trade.quantity,
            exchangeRateLkr = trade.exchangeRateLkr
        )
        if (!pnl.isValid) return Resource.Error(pnl.errorMessage ?: "P&L calculation failed")
        val tradeWithPnL = trade.copy(
            realisedPnLUsd = pnl.realisedPnLUsd,
            realisedPnLLkr = pnl.realisedPnLLkr
        )
        return incomeRepository.addCryptoTrade(tradeWithPnL)
    }
}