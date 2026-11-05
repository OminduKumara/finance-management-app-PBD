package com.example.pbdassignment.feature.expense.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val amount: Double,
    val currencyCode: String,
    val categoryId: String,
    val categoryName: String,
    val dateMillis: Long,
    val isCommitted: Boolean,
    val paymentMethod: String,
    val notes: String
)