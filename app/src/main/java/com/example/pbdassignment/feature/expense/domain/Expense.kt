package com.example.pbdassignment.feature.expense.domain

data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val currencyCode: String = "LKR",
    val categoryId: String,
    val categoryName: String,
    val dateMillis: Long,
    val isCommitted: Boolean,
    val paymentMethod: String,
    val notes: String = ""
)
