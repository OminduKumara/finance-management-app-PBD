package com.example.pbdassignment.feature.expense.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.pbdassignment.feature.expense.components.ExpenseCard

import androidx.compose.material3.Button
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun ExpenseListScreen(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier,
    onAddExpenseClick: () -> Unit = {}
) {
    val expenses by viewModel.expenses.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "My Expenses",
            style = MaterialTheme.typography.headlineMedium
        )
        Button(
            onClick = onAddExpenseClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Expense")
        }

        if (expenses.isEmpty()) {
            Text("No expenses yet.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = expenses,
                    key = { it.id }
                ) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onDeleteClick = { expenseId ->
                            viewModel.deleteExpense(expenseId)
                        }
                    )
                }
            }
        }
    }
}
