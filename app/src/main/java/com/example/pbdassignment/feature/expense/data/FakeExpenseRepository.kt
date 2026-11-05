
package com.example.pbdassignment.feature.expense.data

import com.example.pbdassignment.feature.expense.domain.Expense
import com.example.pbdassignment.feature.expense.domain.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeExpenseRepository : ExpenseRepository {

    private val expenses = MutableStateFlow(
        listOf(
            Expense(
                id = "expense-1",
                title = "Lunch",
                amount = 850.0,
                categoryId = "food",
                categoryName = "Food",
                dateMillis = System.currentTimeMillis(),
                isCommitted = false,
                paymentMethod = "Cash"
            ),
            Expense(
                id = "expense-2",
                title = "Internet package",
                amount = 2490.0,
                categoryId = "bills",
                categoryName = "Bills",
                dateMillis = System.currentTimeMillis(),
                isCommitted = true,
                paymentMethod = "Card"
            )
        )
    )

    override fun observeExpenses(): Flow<List<Expense>> =
        expenses.asStateFlow()

    override suspend fun addExpense(expense: Expense) {
        expenses.update { current ->
            current + expense
        }
    }

    override suspend fun updateExpense(expense: Expense) {
        expenses.update { current ->
            current.map { existing ->
                if (existing.id == expense.id) expense else existing
            }
        }
    }

    override suspend fun deleteExpense(id: String) {
        expenses.update { current ->
            current.filterNot { it.id == id }
        }
    }
}
