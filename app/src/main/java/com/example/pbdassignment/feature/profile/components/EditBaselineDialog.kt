package com.example.pbdassignment.feature.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.example.pbdassignment.core.model.FinancialBaseline

@Composable
fun EditBaselineDialog(
    currentBaseline: FinancialBaseline,
    onDismiss: () -> Unit,
    onConfirm: (FinancialBaseline) -> Unit
) {
    var salaryText by remember { mutableStateOf(currentBaseline.baseSalary.toInt().toString()) }
    var rentText by remember { mutableStateOf(currentBaseline.committedRent.toInt().toString()) }
    var gymText by remember { mutableStateOf(currentBaseline.committedGym.toInt().toString()) }
    var discretionaryText by remember { mutableStateOf(currentBaseline.discretionaryBudgetCap.toInt().toString()) }
    var savingsText by remember { mutableStateOf(currentBaseline.targetSavingsRate.toInt().toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Financial Baselines", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = salaryText,
                    onValueChange = { salaryText = it },
                    label = { Text("Base Monthly Salary (LKR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rentText,
                        onValueChange = { rentText = it },
                        label = { Text("Rent (LKR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = gymText,
                        onValueChange = { gymText = it },
                        label = { Text("Gym / Debits") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = discretionaryText,
                        onValueChange = { discretionaryText = it },
                        label = { Text("Discretionary Cap") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = savingsText,
                        onValueChange = { savingsText = it },
                        label = { Text("Monthly Savings") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
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
                    val salary = salaryText.toDoubleOrNull()
                    val rent = rentText.toDoubleOrNull()
                    val gym = gymText.toDoubleOrNull()
                    val disc = discretionaryText.toDoubleOrNull()
                    val savings = savingsText.toDoubleOrNull()

                    if (salary == null || rent == null || gym == null || disc == null || savings == null) {
                        errorMessage = "Please enter valid numbers for all baselines"
                    } else {
                        onConfirm(
                            currentBaseline.copy(
                                baseSalary = salary,
                                committedRent = rent,
                                committedGym = gym,
                                discretionaryBudgetCap = disc,
                                targetSavingsRate = savings
                            )
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Baselines")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
