package com.example.pbdassignment.feature.income.domain

data class IncomeRecord(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val source: IncomeSource = IncomeSource.SALARY,
    val currency: CurrencyType = CurrencyType.LKR,
    val originalAmount: Double = 0.0,
    val exchangeRate: Double = 1.0,
    val convertedAmountLkr: Double = 0.0,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val note: String = "",

    val baseSalaryLkr: Double? = null,
    val bonusLkr: Double? = null,

    val clientName: String? = null,
    val invoiceNumber: String? = null,
    val milestoneStatus: MilestoneStatus? = null,

    val linkedCryptoTradeId: String? = null
)
enum class IncomeSource(val displayName: String) {
    SALARY("Salary"),
    FREELANCE("Freelance"),
    ADSENSE("AdSense (USD)"),
    CRYPTO("Crypto P&L")
}
enum class CurrencyType(val symbol: String) {
    LKR("LKR"),
    USD("USD")
}
enum class MilestoneStatus(val displayName: String) {
    PENDING("Pending"),
    INVOICED("Invoiced"),
    RECEIVED("Received")
}