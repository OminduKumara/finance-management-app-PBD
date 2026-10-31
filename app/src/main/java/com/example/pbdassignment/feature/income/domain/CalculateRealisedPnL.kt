package com.example.pbdassignment.feature.income.domain

import java.math.BigDecimal
import java.math.RoundingMode
import com.example.pbdassignment.feature.income.domain.CryptoTradeRecord

class CalculateRealisedPnL {
    data class PnLResult(
        val realisedPnLUsd: Double,
        val realisedPnLLkr: Double,
        val isProfit: Boolean,
        val isValid: Boolean,
        val errorMessage: String? = null
    )
    data class PortfolioPnLResult(
        val totalRealisedPnLUsd: Double,
        val totalRealisedPnLLkr: Double,
        val totalTrades: Int,
        val profitableTrades: Int,
        val lossyTrades: Int,
        val isNetProfit: Boolean
    )

    operator fun invoke(
        buyPriceUsd: Double,
        sellPriceUsd: Double,
        quantity: Double,
        exchangeRateLkr: Double
    ): PnLResult {
        if (quantity <= 0) {
            return PnLResult(0.0, 0.0, false, false, "Quantity must be greater than zero")
        }
        if (buyPriceUsd < 0 || sellPriceUsd < 0) {
            return PnLResult(0.0, 0.0, false, false, "Price cannot be negative")
        }
        if (exchangeRateLkr <= 0) {
            return PnLResult(0.0, 0.0, false, false, "Exchange rate must be greater than zero")
        }
        val pnlUsd = BigDecimal(sellPriceUsd - buyPriceUsd)
            .multiply(BigDecimal(quantity))
            .setScale(4, RoundingMode.HALF_UP)
            .toDouble()
        val pnlLkr = BigDecimal(pnlUsd)
            .multiply(BigDecimal(exchangeRateLkr))
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
        return PnLResult(
            realisedPnLUsd = pnlUsd,
            realisedPnLLkr = pnlLkr,
            isProfit = pnlUsd >= 0,
            isValid = true
        )
    }

    fun calculatePortfolioPnL(trades: List<CryptoTradeRecord>): PortfolioPnLResult {
        val totalUsd = trades.sumOf { it.realisedPnLUsd }
        val totalLkr = trades.sumOf { it.realisedPnLLkr }
        return PortfolioPnLResult(
            totalRealisedPnLUsd = BigDecimal(totalUsd).setScale(4, RoundingMode.HALF_UP).toDouble(),
            totalRealisedPnLLkr = BigDecimal(totalLkr).setScale(2, RoundingMode.HALF_UP).toDouble(),
            totalTrades = trades.size,
            profitableTrades = trades.count { it.realisedPnLUsd >= 0 },
            lossyTrades = trades.count { it.realisedPnLUsd < 0 },
            isNetProfit = totalUsd >= 0
        )
    }
}