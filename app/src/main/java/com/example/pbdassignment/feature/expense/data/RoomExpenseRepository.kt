package com.example.pbdassignment.feature.expense.data

import com.example.pbdassignment.feature.expense.domain.Expense
import com.example.pbdassignment.feature.expense.domain.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomExpenseRepository(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override fun observeExpenses(): Flow<List<Expense>> {
        return expenseDao.observeExpenses().map { expenses ->
            expenses.map { it.toExpense() }
        }
    }

    override suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense.toExpenseEntity())
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toExpenseEntity())
    }

    override suspend fun deleteExpense(id: String) {
        expenseDao.deleteExpense(id)
    }
}