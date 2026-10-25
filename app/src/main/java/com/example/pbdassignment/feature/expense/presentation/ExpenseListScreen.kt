package com.example.pbdassignment.feature.expense.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpenseListScreen(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
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
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = expense.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${expense.currencyCode} ${
                                    NumberFormat.getNumberInstance(
                                        Locale.getDefault()
                                    ).format(expense.amount)
                                }"
                            )
                            Text("Category: ${expense.categoryName}")
                            Text(
                                if (expense.isCommitted) {
                                    "Committed"
                                } else {
                                    "Discretionary"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
