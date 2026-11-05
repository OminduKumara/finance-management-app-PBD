package com.example.pbdassignment.feature.income.domain

data class CryptoTradeRecord(
    val id: String = "",
    val userId: String = "",
    val assetSymbol: String = "",
    val buyPriceUsd: Double = 0.0,
    val sellPriceUsd: Double = 0.0,
    val quantity: Double = 0.0,
    val realisedPnLUsd: Double = 0.0,
    val realisedPnLLkr: Double = 0.0,
    val exchangeRateLkr: Double = 305.0,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val exchange: String = "Binance"
)