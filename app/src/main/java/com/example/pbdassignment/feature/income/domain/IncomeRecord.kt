package com.example.pbdassignment.feature.income.domain

data class IncomeRecord(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val source: IncomeSource = IncomeSource.SALARY,
    val currency: CurrencyType = CurrencyType.LKR,
    val originalAmount: Double = 0.0,       // Amount in original currency (USD for AdSense)
    val exchangeRate: Double = 1.0,          // e.g. 305.50 LKR per 1 USD
    val convertedAmountLkr: Double = 0.0,    // Final amount in LKR = originalAmount * exchangeRate
    val dateTimestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    // Salary-specific
    val baseSalaryLkr: Double? = null,
    val bonusLkr: Double? = null,
    // Freelance-specific
    val clientName: String? = null,
    val invoiceNumber: String? = null,
    val milestoneStatus: MilestoneStatus? = null,
    // Crypto P&L is stored via CryptoTradeRecord (separate collection)
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