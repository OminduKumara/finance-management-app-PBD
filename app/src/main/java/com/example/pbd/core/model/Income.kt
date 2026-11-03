package com.example.pbd.core.model

import java.util.UUID

object IncomeStreams {
    const val SALARY = "Salary"
    const val FREELANCE = "Freelance"
    const val ADSENSE = "Google AdSense"
    const val CRYPTO = "Crypto"
    const val OTHER = "Custom"

    val defaultStreams = listOf(SALARY, FREELANCE, ADSENSE, CRYPTO, OTHER)
}

enum class InvoiceStatus(val displayName: String) {
    RECEIVED("Received"),
    INVOICED_PENDING("Pending"),
    OVERDUE("Overdue (>14d)"),
    SETTLED("Settled")
}

data class IncomeItem(
    val id: String = UUID.randomUUID().toString(),
    val streamType: String = IncomeStreams.SALARY,
    val title: String,
    val clientOrSource: String,
    val rawAmount: Double,
    val currency: String = "LKR",
    val exchangeRate: Double = 1.0,
    val lkrAmount: Double = rawAmount * exchangeRate,
    val date: Long = System.currentTimeMillis(),
    val invoiceStatus: InvoiceStatus = InvoiceStatus.RECEIVED,
    val dueDate: Long? = null,
    val isSweptToGoal: Boolean = false,
    val goalSweptAmount: Double = 0.0
)
