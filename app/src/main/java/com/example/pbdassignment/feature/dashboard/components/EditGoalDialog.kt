package com.example.pbdassignment.feature.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.pbdassignment.core.model.GoalItem

@Composable
fun EditGoalDialog(
    currentGoal: GoalItem,
    onDismiss: () -> Unit,
    onConfirm: (title: String, targetAmount: Double, currentSaved: Double, months: Int) -> Unit
) {
    var title by remember { mutableStateOf(currentGoal.title) }
    var targetText by remember { mutableStateOf(currentGoal.targetAmount.toInt().toString()) }
    var savedText by remember { mutableStateOf(currentGoal.currentSaved.toInt().toString()) }
    var monthsText by remember { mutableStateOf(currentGoal.targetMonths.toString()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Set Financial Target Goal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Target Goal Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Target Amount (LKR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = savedText,
                        onValueChange = { savedText = it },
                        label = { Text("Current Saved (LKR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = monthsText,
                        onValueChange = { monthsText = it },
                        label = { Text("Timeline (Months)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetText.toDoubleOrNull()
                    val saved = savedText.toDoubleOrNull()
                    val months = monthsText.toIntOrNull()

                    if (title.isBlank() || target == null || target <= 0 || saved == null || saved < 0 || months == null || months <= 0) {
                        errorMsg = "Please enter valid title, positive target, and months"
                    } else {
                        onConfirm(title, target, saved, months)
                    }
                }
            ) {
                Text("Save Target")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
