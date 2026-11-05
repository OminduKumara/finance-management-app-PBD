package com.example.pbdassignment.feature.recurring.data

import com.example.pbdassignment.feature.recurring.domain.RecurringExpense

fun RecurringExpenseEntity.toDomain(): RecurringExpense {
    return RecurringExpense(
        id = id,
        title = title,
        amount = amount,
        currencyCode = currencyCode,
        categoryId = categoryId,
        categoryName = categoryName,
        paymentMethod = paymentMethod,
        frequency = frequency,
        startDateMillis = startDateMillis,
        endDateMillis = endDateMillis,
        isActive = isActive,
        notes = notes,
        lastGeneratedDateMillis = lastGeneratedDateMillis
    )
}

fun RecurringExpense.toEntity(): RecurringExpenseEntity {
    return RecurringExpenseEntity(
        id = id,
        title = title,
        amount = amount,
        currencyCode = currencyCode,
        categoryId = categoryId,
        categoryName = categoryName,
        paymentMethod = paymentMethod,
        frequency = frequency,
        startDateMillis = startDateMillis,
        endDateMillis = endDateMillis,
        isActive = isActive,
        notes = notes,
        lastGeneratedDateMillis = lastGeneratedDateMillis
    )
}