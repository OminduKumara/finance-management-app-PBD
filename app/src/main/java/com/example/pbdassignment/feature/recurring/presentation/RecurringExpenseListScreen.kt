package com.example.pbdassignment.feature.recurring.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pbdassignment.feature.recurring.domain.RecurringExpense

@Composable
fun RecurringExpenseListScreen(
    expenses: List<RecurringExpense>,
    onAddClick: () -> Unit,
    onEditClick: (RecurringExpense) -> Unit,
    onDeleteClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recurring Expenses",
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(onClick = onBack) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Recurring Expense")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (expenses.isEmpty()) {
            Text(
                text = "No recurring expenses yet.",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expenses, key = { it.id }) { expense ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = expense.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("${expense.currencyCode} ${expense.amount}")
                                Text("Category: ${expense.categoryName}")
                                Text("Frequency: ${expense.frequency}")
                                Text(
                                    text = if (expense.isActive) "Active" else "Paused",
                                    color = if (expense.isActive) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    }
                                )
                            }

                            Column {
                                TextButton(onClick = { onEditClick(expense) }) {
                                    Text("Edit")
                                }

                                TextButton(onClick = { onDeleteClick(expense.id) }) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}