package com.example.pbdassignment.feature.income.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
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
import com.example.pbdassignment.core.model.IncomeStreams
import com.example.pbdassignment.ui.theme.BrandPrimary

@Composable
fun AddIncomeDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        streamType: String,
        title: String,
        source: String,
        amount: Double,
        currency: String,
        exchangeRate: Double,
        isPending: Boolean
    ) -> Unit
) {
    var selectedStream by remember { mutableStateOf(IncomeStreams.FREELANCE) }
    var customStreamName by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isUsd by remember { mutableStateOf(false) }
    var exchangeRateText by remember { mutableStateOf("302.50") }
    var isPendingInvoice by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dialogTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = BrandPrimary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = BrandPrimary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        cursorColor = BrandPrimary
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Add Income / Inflow",
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
                Text(
                    text = "Income Stream",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IncomeStreams.defaultStreams.forEach { stream ->
                        val isSelected = selectedStream == stream
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedStream = stream
                                isUsd = stream == IncomeStreams.ADSENSE
                                isPendingInvoice = stream == IncomeStreams.FREELANCE
                                if (title.isBlank() && stream != IncomeStreams.OTHER) {
                                    title = when (stream) {
                                        IncomeStreams.SALARY -> "Monthly Base Salary"
                                        IncomeStreams.FREELANCE -> "Freelance Web Project"
                                        IncomeStreams.ADSENSE -> "AdSense Blog Earnings"
                                        IncomeStreams.CRYPTO -> "Crypto Trading Gain"
                                        else -> ""
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = stream,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = BrandPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }

                if (selectedStream == IncomeStreams.OTHER) {
                    OutlinedTextField(
                        value = customStreamName,
                        onValueChange = { customStreamName = it },
                        label = { Text("Custom Stream Name") },
                        placeholder = { Text("e.g. Tutoring, Consulting, Dividends") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = dialogTextFieldColors
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Income Title") },
                    placeholder = { Text("e.g. React Milestone, Bonus, Client Fee") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = dialogTextFieldColors
                )

                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("Payer / Client / Source") },
                    placeholder = { Text("e.g. Local SME, Startup, Binance") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = dialogTextFieldColors
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (isUsd) "Amount in USD ($)" else "Amount in LKR") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = dialogTextFieldColors
                )

                // Currency Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isUsd,
                        onClick = { isUsd = false },
                        label = {
                            Text(
                                "LKR (Local)",
                                fontSize = 12.sp,
                                fontWeight = if (!isUsd) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = BrandPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (!isUsd) BrandPrimary else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = isUsd,
                        onClick = { isUsd = true },
                        label = {
                            Text(
                                "USD ($)",
                                fontSize = 12.sp,
                                fontWeight = if (isUsd) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = BrandPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isUsd) BrandPrimary else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isUsd) {
                    OutlinedTextField(
                        value = exchangeRateText,
                        onValueChange = { exchangeRateText = it },
                        label = { Text("USD to LKR Exchange Rate") },
                        placeholder = { Text("302.50") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = dialogTextFieldColors
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPendingInvoice,
                        onCheckedChange = { isPendingInvoice = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = BrandPrimary,
                            checkmarkColor = Color.White
                        )
                    )
                    Text(
                        text = "Unpaid / Pending Invoice",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    val rate = if (isUsd) exchangeRateText.toDoubleOrNull() ?: 302.50 else 1.0
                    val finalStream = if (selectedStream == IncomeStreams.OTHER) {
                        customStreamName.ifBlank { "Custom Inflow" }
                    } else {
                        selectedStream
                    }

                    if (title.isBlank() || source.isBlank() || amount == null || amount <= 0) {
                        errorMessage = "Please enter valid title, source, and positive amount"
                    } else {
                        onConfirm(
                            finalStream,
                            title,
                            source,
                            amount,
                            if (isUsd) "USD" else "LKR",
                            rate,
                            isPendingInvoice
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text(
                    text = "Save Income",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}
