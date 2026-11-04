package com.example.pbdassignment.core.model

import java.util.UUID

enum class ExpenseNature(val label: String) {
    COMMITTED("Committed (Fixed)"),
    DISCRETIONARY("Discretionary (Lifestyle)")
}

enum class PaymentSource(val label: String) {
    PRIMARY_CARD("Primary Card"),
    CASH("Cash"),
    SECONDARY_ACCOUNT("Secondary Bank")
}

object DefaultExpenseCategories {
    const val HOUSING = "Rent & Housing"
    const val TRANSPORT = "Transport (PickMe)"
    const val FOOD = "Food & Dining"
    const val UTILITIES = "Utilities"
    const val SUBSCRIPTIONS = "Subscriptions"
    const val HEALTH_GYM = "Gym & Fitness"
    const val OTHER = "Custom"

    val list = listOf(TRANSPORT, FOOD, HOUSING, UTILITIES, SUBSCRIPTIONS, HEALTH_GYM, OTHER)
}

data class ExpenseItem(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: String = DefaultExpenseCategories.FOOD,
    val nature: ExpenseNature = ExpenseNature.DISCRETIONARY,
    val paymentSource: PaymentSource = PaymentSource.PRIMARY_CARD,
    val merchantPreset: String? = null,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
