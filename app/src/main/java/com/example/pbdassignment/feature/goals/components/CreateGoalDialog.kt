package com.example.pbdassignment.feature.goals.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pbdassignment.ui.theme.BrandPrimary

@Composable
fun CreateGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        targetAmount: Double,
        months: Int,
        initialDeposit: Double,
        note: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetAmountText by remember { mutableStateOf("") }
    var targetMonthsText by remember { mutableStateOf("12") }
    var initialDepositText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

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
                text = "Create New Goal",
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
                    text = "Define your financial milestone & savings target",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Goal Title
                Column {
                    Text(
                        text = "Goal Title *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = null
                        },
                        placeholder = { Text("e.g. MacBook Pro M4 (14-inch)") },
                        isError = titleError != null,
                        supportingText = titleError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = dialogTextFieldColors
                    )
                }

                // Target Amount
                Column {
                    Text(
                        text = "Target Amount (LKR) *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = targetAmountText,
                        onValueChange = {
                            targetAmountText = it
                            amountError = null
                        },
                        placeholder = { Text("e.g. 490000") },
                        prefix = { Text("LKR ", color = MaterialTheme.colorScheme.onSurface) },
                        isError = amountError != null,
                        supportingText = amountError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = dialogTextFieldColors
                    )
                }

                // Timeline in Months
                Column {
                    Text(
                        text = "Timeline (Months)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = targetMonthsText,
                        onValueChange = { targetMonthsText = it },
                        placeholder = { Text("12") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = dialogTextFieldColors
                    )
                }

                // Initial Seed Deposit
                Column {
                    Text(
                        text = "Initial Deposit (Optional)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = initialDepositText,
                        onValueChange = { initialDepositText = it },
                        placeholder = { Text("e.g. 10000") },
                        prefix = { Text("LKR ", color = MaterialTheme.colorScheme.onSurface) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = dialogTextFieldColors
                    )
                }

                // Notes
                Column {
                    Text(
                        text = "Purpose or Motivation (Optional)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("e.g. Primary driver for mobile dev & freelance builds") },
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = dialogTextFieldColors
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false
                    if (title.isBlank()) {
                        titleError = "Goal title cannot be blank"
                        hasError = true
                    }
                    val targetAmount = targetAmountText.toDoubleOrNull()
                    if (targetAmount == null || targetAmount <= 0) {
                        amountError = "Enter a valid positive target amount"
                        hasError = true
                    }
                    val months = targetMonthsText.toIntOrNull() ?: 12
                    val initialDeposit = initialDepositText.toDoubleOrNull() ?: 0.0

                    if (!hasError && targetAmount != null) {
                        onConfirm(
                            title.trim(),
                            targetAmount,
                            months,
                            initialDeposit,
                            note.trim()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Goal", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

