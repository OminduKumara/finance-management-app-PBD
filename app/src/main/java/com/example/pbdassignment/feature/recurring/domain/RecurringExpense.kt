package com.example.pbdassignment.feature.recurring.domain

data class RecurringExpense(
    val id: String,
    val title: String,
    val amount: Double,
    val currencyCode: String = "LKR",
    val categoryId: String,
    val categoryName: String,
    val paymentMethod: String,
    val frequency: String,
    val startDateMillis: Long,
    val endDateMillis: Long? = null,
    val isActive: Boolean = true,
    val notes: String = "",
    val lastGeneratedDateMillis: Long? = null
)