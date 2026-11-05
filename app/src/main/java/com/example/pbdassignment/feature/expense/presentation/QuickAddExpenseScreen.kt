package com.example.pbdassignment.feature.expense.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.pbdassignment.feature.expense.domain.Expense
import com.example.pbdassignment.ui.theme.BrandPrimary

@Composable
fun QuickAddExpenseScreen(
    modifier: Modifier = Modifier,
    initialExpense: Expense? = null,
    categories: List<Category>,
    onSave: (title: String, amount: Double, categoryName: String, paymentMethod: String, isCommitted: Boolean) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember(initialExpense?.id) {
        mutableStateOf(initialExpense?.title ?: "")
    }

    var amountText by remember(initialExpense?.id) {
        mutableStateOf(if (initialExpense != null) initialExpense.amount.toString() else "")
    }

    var selectedCategory by remember(initialExpense?.id, categories) {
        mutableStateOf(
            initialExpense?.categoryName
                ?: categories.firstOrNull()?.name
                ?: "General"
        )
    }

    var isCommitted by remember(initialExpense?.id) {
        mutableStateOf(initialExpense?.isCommitted ?: false)
    }

    val paymentMethods = listOf(
        "Credit Card",
        "Debit Card",
        "Cash",
        "Bank Transfer",
        "Other"
    )

    var selectedPaymentMethod by remember(initialExpense?.id) {
        mutableStateOf(initialExpense?.paymentMethod ?: "Credit Card")
    }

    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var paymentMenuExpanded by remember { mutableStateOf(false) }

    val parsedAmount = amountText.toDoubleOrNull()
    val isAmountValid = parsedAmount != null && parsedAmount > 0
    val isFormValid = title.isNotBlank() && isAmountValid

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simple Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (initialExpense == null) "New Expense" else "Edit Expense",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp
            )

            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Form Fields in Clean Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title Field
                Column {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("e.g. Netflix, Keells, Rent", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedBorderColor = Color(0xFF111827)
                        )
                    )
                }

                // Amount Field
                Column {
                    Text(
                        text = "Amount (LKR)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        placeholder = { Text("0.00", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedBorderColor = Color(0xFF111827)
                        )
                    )
                }

                // Category Dropdown
                Column {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box {
                        OutlinedButton(
                            onClick = { categoryMenuExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111827))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedCategory, fontSize = 14.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF6B7280))
                            }
                        }

                        DropdownMenu(
                            expanded = categoryMenuExpanded,
                            onDismissRequest = { categoryMenuExpanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name, fontSize = 14.sp) },
                                    onClick = {
                                        selectedCategory = category.name
                                        categoryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Nature: Fixed vs Lifestyle (Minimalist Toggle)
                Column {
                    Text(
                        text = "Type",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isCommitted,
                            onClick = { isCommitted = true },
                            label = { Text("Fixed (Bill / Rent)", fontSize = 12.sp) },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1F2937),
                                selectedLabelColor = Color.White,
                                containerColor = Color.Transparent,
                                labelColor = Color(0xFF4B5563)
                            ),
                            border = BorderStroke(1.dp, if (isCommitted) Color(0xFF1F2937) else Color(0xFFD1D5DB)),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = !isCommitted,
                            onClick = { isCommitted = false },
                            label = { Text("Lifestyle", fontSize = 12.sp) },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1F2937),
                                selectedLabelColor = Color.White,
                                containerColor = Color.Transparent,
                                labelColor = Color(0xFF4B5563)
                            ),
                            border = BorderStroke(1.dp, if (!isCommitted) Color(0xFF1F2937) else Color(0xFFD1D5DB)),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Payment Method Dropdown
                Column {
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box {
                        OutlinedButton(
                            onClick = { paymentMenuExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111827))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedPaymentMethod, fontSize = 14.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF6B7280))
                            }
                        }

                        DropdownMenu(
                            expanded = paymentMenuExpanded,
                            onDismissRequest = { paymentMenuExpanded = false }
                        ) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method, fontSize = 14.sp) },
                                    onClick = {
                                        selectedPaymentMethod = method
                                        paymentMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF374151))
            ) {
                Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = {
                    if (parsedAmount != null) {
                        onSave(title.trim(), parsedAmount, selectedCategory, selectedPaymentMethod, isCommitted)
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    disabledContainerColor = Color(0xFFE5E7EB)
                )
            ) {
                Text(
                    text = if (initialExpense == null) "Save" else "Update",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isFormValid) Color.White else Color(0xFF9CA3AF)
                )
            }
        }
    }
}
