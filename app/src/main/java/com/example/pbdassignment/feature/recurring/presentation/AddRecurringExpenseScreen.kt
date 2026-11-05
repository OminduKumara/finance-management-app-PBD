package com.example.pbdassignment.feature.recurring.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pbdassignment.feature.category.domain.Category
import com.example.pbdassignment.feature.recurring.domain.RecurrenceFrequency
import com.example.pbdassignment.feature.recurring.domain.RecurringExpense
import com.example.pbdassignment.ui.theme.BrandPrimary
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
            existingExpense?.categoryName ?: categories.firstOrNull()?.name ?: "General"
        )
    }

    var selectedPaymentMethod by remember(existingExpense) {
        mutableStateOf(existingExpense?.paymentMethod ?: "Credit Card")
    }

    var selectedFrequency by remember(existingExpense) {
        val initialFreq = existingExpense?.frequency?.let { freqName ->
            RecurrenceFrequency.entries.firstOrNull { it.name.equals(freqName, ignoreCase = true) }
        } ?: RecurrenceFrequency.MONTHLY
        mutableStateOf(initialFreq)
    }

    var notes by remember(existingExpense) {
        mutableStateOf(existingExpense?.notes ?: "")
    }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var paymentDropdownExpanded by remember { mutableStateOf(false) }
    var frequencyDropdownExpanded by remember { mutableStateOf(false) }

    val paymentMethods = listOf("Credit Card", "Debit Card", "Cash", "Bank Transfer", "Other")

    val parsedAmount = amount.toDoubleOrNull()
    val isAmountValid = parsedAmount != null && parsedAmount > 0
    val isFormValid = title.isNotBlank() && isAmountValid

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = BrandPrimary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedLabelColor = BrandPrimary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        cursorColor = BrandPrimary
    )

    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = if (existingExpense == null) "New Recurring Expense" else "Edit Recurring Expense",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title
                Column {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = {
                            Text(
                                "e.g. Apartment Rent, Internet, Gym",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = textFieldColors
                    )
                }

                // Amount
                Column {
                    Text(
                        text = "Amount (LKR)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = {
                            Text(
                                "0.00",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = textFieldColors
                    )
                }

                // Frequency Dropdown
                Column {
                    Text(
                        text = "Frequency",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box {
                        OutlinedButton(
                            onClick = { frequencyDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    selectedFrequency.displayName,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = frequencyDropdownExpanded,
                            onDismissRequest = { frequencyDropdownExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            RecurrenceFrequency.entries.forEach { freq ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            freq.displayName,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        selectedFrequency = freq
                                        frequencyDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Category Dropdown
                Column {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box {
                        OutlinedButton(
                            onClick = { categoryDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    selectedCategory,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            category.name,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        selectedCategory = category.name
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Payment Method Dropdown
                Column {
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box {
                        OutlinedButton(
                            onClick = { paymentDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    selectedPaymentMethod,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = paymentDropdownExpanded,
                            onDismissRequest = { paymentDropdownExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            method,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        selectedPaymentMethod = method
                                        paymentDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Notes
                Column {
                    Text(
                        text = "Notes (Optional)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = {
                            Text(
                                "Add any extra details...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = false,
                        maxLines = 3,
                        colors = textFieldColors
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (parsedAmount != null) {
                        val category = categories.firstOrNull { it.name == selectedCategory }
                        val categoryId = category?.id ?: selectedCategory.lowercase()
                        onSave(
                            RecurringExpense(
                                id = existingExpense?.id ?: UUID.randomUUID().toString(),
                                title = title.trim(),
                                amount = parsedAmount,
                                currencyCode = existingExpense?.currencyCode ?: "LKR",
                                categoryId = categoryId,
                                categoryName = selectedCategory,
                                paymentMethod = selectedPaymentMethod,
                                frequency = selectedFrequency.name,
                                startDateMillis = existingExpense?.startDateMillis ?: System.currentTimeMillis(),
                                endDateMillis = existingExpense?.endDateMillis,
                                isActive = existingExpense?.isActive ?: true,
                                notes = notes.trim(),
                                lastGeneratedDateMillis = existingExpense?.lastGeneratedDateMillis
                            )
                        )
                    }
                },
                enabled = isFormValid,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = if (existingExpense == null) "Save" else "Update",
                    fontWeight = FontWeight.Bold,
                    color = if (isFormValid) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(
                    "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}
