
package com.example.pbdassignment.feature.expense.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickAddExpenseScreen(
    modifier: Modifier = Modifier,
    initialExpense: com.example.pbdassignment.feature.expense.domain.Expense? = null,
    onSave: (title: String, amount: Double, categoryName: String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember(initialExpense?.id) {
        mutableStateOf(initialExpense?.title ?: "")
    }

    var amountText by remember(initialExpense?.id) {
        mutableStateOf(initialExpense?.amount?.toString() ?: "")
    }

    val categories = listOf(
        "Food",
        "Transport",
        "Shopping",
        "Bills",
        "Entertainment",
        "Health",
        "Other"
    )

    var selectedCategory by remember(initialExpense?.id) {
        mutableStateOf(initialExpense?.categoryName ?: "Other")
    }

    var categoryMenuExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (initialExpense == null) {
                "Add Expense"
            } else {
                "Edit Expense"
            },
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Expense name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (title.isNotEmpty() && title.isBlank()) {
            Text(
                text = "Expense name cannot contain only spaces.",
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Amount (LKR)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Column {
            OutlinedButton(
                onClick = { categoryMenuExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Category: $selectedCategory")
            }

            DropdownMenu(
                expanded = categoryMenuExpanded,
                onDismissRequest = { categoryMenuExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            categoryMenuExpanded = false
                        }
                    )
                }
            }
        }

        if (amountText.isNotBlank() &&
            (amountText.toDoubleOrNull() == null ||
                    amountText.toDoubleOrNull()!! <= 0.0)
        ) {
            Text(
                text = "Enter a valid amount greater than zero.",
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                val amount = amountText.toDoubleOrNull()
                if (title.isNotBlank() && amount != null && amount > 0) {
                    onSave(title.trim(), amount, selectedCategory)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() &&
                    (amountText.toDoubleOrNull() ?: 0.0) > 0.0
        ) {
            Text("Save Expense")
        }
        Button(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}
