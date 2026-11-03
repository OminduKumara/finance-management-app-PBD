package com.example.pbd.feature_income.components

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pbd.core.model.IncomeStreams

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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Income / Inflow", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Income Stream", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IncomeStreams.defaultStreams.forEach { stream ->
                        FilterChip(
                            selected = selectedStream == stream,
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
                            label = { Text(stream) }
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Income Title") },
                    placeholder = { Text("e.g. React Milestone, Bonus, Client Fee") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("Payer / Client / Source") },
                    placeholder = { Text("e.g. Local SME, Startup, Binance") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (isUsd) "Amount in USD ($)" else "Amount in LKR") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Currency Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isUsd,
                        onClick = { isUsd = false },
                        label = { Text("LKR (Local)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = isUsd,
                        onClick = { isUsd = true },
                        label = { Text("USD ($)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isUsd) {
                    OutlinedTextField(
                        value = exchangeRateText,
                        onValueChange = { exchangeRateText = it },
                        label = { Text("USD to LKR Exchange Rate") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPendingInvoice,
                        onCheckedChange = { isPendingInvoice = it }
                    )
                    Text("Unpaid / Pending Invoice", style = MaterialTheme.typography.bodyMedium)
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
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
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Income")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
