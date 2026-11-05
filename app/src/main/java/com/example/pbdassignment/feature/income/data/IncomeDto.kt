package com.example.pbdassignment.feature.income.data

data class IncomeDto(
    var id: String = "",
    var userId: String = "",
    var title: String = "",
    var source: String = "",
    var currency: String = "LKR",
    var originalAmount: Double = 0.0,
    var exchangeRate: Double = 1.0,
    var convertedAmountLkr: Double = 0.0,
    var dateTimestamp: Long = 0L,
    var note: String = "",
    var baseSalaryLkr: Double? = null,
    var bonusLkr: Double? = null,
    var clientName: String? = null,
    var invoiceNumber: String? = null,
    var milestoneStatus: String? = null,
    var linkedCryptoTradeId: String? = null
)