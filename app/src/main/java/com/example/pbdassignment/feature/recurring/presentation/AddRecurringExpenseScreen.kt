package com.example.pbdassignment.feature.recurring.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pbdassignment.feature.category.domain.Category
import com.example.pbdassignment.feature.recurring.domain.RecurrenceFrequency
import com.example.pbdassignment.feature.recurring.domain.RecurringExpense
import java.util.UUID

@Composable
fun AddRecurringExpenseScreen(
    categories: List<Category>,
    onSave: (RecurringExpense) -> Unit,
    onCancel: () -> Unit,
    existingExpense: RecurringExpense? = null
) {
    var title by remember(existingExpense) {
        mutableStateOf(existingExpense?.title ?: "")
    }

    var amount by remember(existingExpense) {
        mutableStateOf(existingExpense?.amount?.toString() ?: "")
    }

    var selectedCategory by remember(existingExpense, categories) {
        mutableStateOf(
            existingExpense?.categoryName ?: categories.firstOrNull()?.name ?: ""
        )
    }

    var selectedPaymentMethod by remember(existingExpense) {
        mutableStateOf(existingExpense?.paymentMethod ?: "Cash")
    }

    var selectedFrequency by remember(existingExpense) {
        mutableStateOf(
            RecurrenceFrequency.entries.firstOrNull {
                it.name == existingExpense?.frequency
            } ?: RecurrenceFrequency.MONTHLY
        )
    }

    var notes by remember(existingExpense) {
        mutableStateOf(existingExpense?.notes ?: "")
    }

    var categoryExpanded by remember { mutableStateOf(false) }
    var paymentExpanded by remember { mutableStateOf(false) }
    var frequencyExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val paymentMethods = listOf(
        "Cash",
        "Credit Card",
        "Debit Card",
        "Bank Transfer",
        "Mobile Payment"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (existingExpense == null) {
                "Add Recurring Expense"
            } else {
                "Edit Recurring Expense"
            },
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Expense Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (LKR)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Category dropdown
        Box {
            OutlinedButton(
                onClick = { categoryExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (selectedCategory.isBlank()) {
                        "Select Category"
                    } else {
                        "Category: $selectedCategory"
                    }
                )
            }

            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text("${category.icon} ${category.name}") },
                        onClick = {
                            selectedCategory = category.name
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        // Payment method dropdown
        Box {
            OutlinedButton(
                onClick = { paymentExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Payment: $selectedPaymentMethod")
            }

            DropdownMenu(
                expanded = paymentExpanded,
                onDismissRequest = { paymentExpanded = false }
            ) {
                paymentMethods.forEach { method ->
                    DropdownMenuItem(
                        text = { Text(method) },
                        onClick = {
                            selectedPaymentMethod = method
                            paymentExpanded = false
                        }
                    )
                }
            }
        }

        // Frequency dropdown
        Box {
            OutlinedButton(
                onClick = { frequencyExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Frequency: ${selectedFrequency.displayName}")
            }

            DropdownMenu(
                expanded = frequencyExpanded,
                onDismissRequest = { frequencyExpanded = false }
            ) {
                RecurrenceFrequency.entries.forEach { frequency ->
                    DropdownMenuItem(
                        text = { Text(frequency.displayName) },
                        onClick = {
                            selectedFrequency = frequency
                            frequencyExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull()

                when {
                    title.isBlank() ->
                        errorMessage = "Please enter an expense title."

                    parsedAmount == null || !parsedAmount.isFinite() || parsedAmount <= 0 ->
                        errorMessage = "Please enter a valid amount."

                    selectedCategory.isBlank() ->
                        errorMessage = "Please select a category."

                    else -> {
                        val category = categories.firstOrNull {
                            it.name == selectedCategory
                        }

                        if (category == null) {
                            errorMessage = "Please select a valid category."
                        } else {
                            onSave(
                                RecurringExpense(
                                    id = existingExpense?.id ?: UUID.randomUUID().toString(),
                                    title = title.trim(),
                                    amount = parsedAmount,
                                    currencyCode = existingExpense?.currencyCode ?: "LKR",
                                    categoryId = category.id,
                                    categoryName = category.name,
                                    paymentMethod = selectedPaymentMethod,
                                    frequency = selectedFrequency.name,
                                    startDateMillis = existingExpense?.startDateMillis
                                        ?: System.currentTimeMillis(),
                                    endDateMillis = existingExpense?.endDateMillis,
                                    isActive = existingExpense?.isActive ?: true,
                                    notes = notes.trim(),
                                    lastGeneratedDateMillis = existingExpense?.lastGeneratedDateMillis
                                )
                            )
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (existingExpense == null) {
                    "Save Recurring Expense"
                } else {
                    "Update Recurring Expense"
                }
            )
        }

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}