package com.example.pbdassignment.feature.income.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incomes")
data class IncomeEntity(
    @PrimaryKey
    val id: String,
    val streamType: String,
    val title: String,
    val clientOrSource: String,
    val rawAmount: Double,
    val currency: String,
    val exchangeRate: Double,
    val lkrAmount: Double,
    val date: Long,
    val invoiceStatus: String,
    val dueDate: Long? = null,
    val isSweptToGoal: Boolean = false,
    val goalSweptAmount: Double = 0.0
)

