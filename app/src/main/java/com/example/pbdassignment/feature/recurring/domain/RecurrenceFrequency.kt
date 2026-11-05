package com.example.pbdassignment.feature.recurring.domain

enum class RecurrenceFrequency(
    val displayName: String
) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}