package com.example.pbdassignment.feature.income.data

data class CryptoTradeDto(
    var id: String = "",
    var userId: String = "",
    var assetSymbol: String = "",
    var buyPriceUsd: Double = 0.0,
    var sellPriceUsd: Double = 0.0,
    var quantity: Double = 0.0,
    var realisedPnLUsd: Double = 0.0,
    var realisedPnLLkr: Double = 0.0,
    var exchangeRateLkr: Double = 305.0,
    var dateTimestamp: Long = 0L,
    var exchange: String = "Binance"
)