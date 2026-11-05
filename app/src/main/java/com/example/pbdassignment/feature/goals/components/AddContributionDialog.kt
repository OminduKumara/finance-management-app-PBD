package com.example.pbdassignment.feature.goals.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddContributionDialog(
    goalTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, note: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    val presetChips = listOf(
        Pair("5,000", 5000.0),
        Pair("10,000", 10000.0),
        Pair("25,000 (Freelance)", 25000.0),
        Pair("40,000 (Salary)", 40000.0)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add Savings Deposit", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(text = "Target Goal: $goalTitle")
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        inputError = null
                    },
                    label = { Text("Amount (LKR)") },
                    prefix = { Text("LKR ") },
                    isError = inputError != null,
                    supportingText = inputError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Presets
                Text(text = "Quick Inflow Sweeps:", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    presetChips.forEach { (label, value) ->
                        FilterChip(
                            selected = amountText == value.toInt().toString(),
                            onClick = {
                                amountText = value.toInt().toString()
                                inputError = null
                            },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note (Optional)") },
                    placeholder = { Text("e.g. 50% freelance milestone sweep") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        inputError = "Enter a valid amount > 0"
                    } else {
                        onConfirm(amount, noteText)
                    }
                }
            ) {
                Text("Confirm Deposit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
