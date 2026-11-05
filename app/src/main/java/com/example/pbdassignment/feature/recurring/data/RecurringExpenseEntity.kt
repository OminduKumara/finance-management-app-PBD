package com.example.pbdassignment.feature.recurring.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_expenses")
data class RecurringExpenseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val amount: Double,
    val currencyCode: String,
    val categoryId: String,
    val categoryName: String,
    val paymentMethod: String,
    val frequency: String,
    val startDateMillis: Long,
    val endDateMillis: Long?,
    val isActive: Boolean,
    val notes: String,
    val lastGeneratedDateMillis: Long?
)