package com.example.pbdassignment.feature.expense.data

import com.example.pbdassignment.feature.expense.domain.Expense

fun ExpenseEntity.toExpense(): Expense {
    return Expense(
        id = id,
        title = title,
        amount = amount,
        currencyCode = currencyCode,
        categoryId = categoryId,
        categoryName = categoryName,
        dateMillis = dateMillis,
        isCommitted = isCommitted,
        paymentMethod = paymentMethod,
        notes = notes
    )
}

fun Expense.toExpenseEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        title = title,
        amount = amount,
        currencyCode = currencyCode,
        categoryId = categoryId,
        categoryName = categoryName,
        dateMillis = dateMillis,
        isCommitted = isCommitted,
        paymentMethod = paymentMethod,
        notes = notes
    )
}