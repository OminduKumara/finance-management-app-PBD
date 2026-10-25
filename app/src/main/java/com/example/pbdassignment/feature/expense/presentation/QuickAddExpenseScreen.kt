
package com.example.pbdassignment.feature.expense.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
    onSave: (title: String, amount: Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Add Expense",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Expense name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Amount (LKR)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

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
                    onSave(title.trim(), amount)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() &&
                    (amountText.toDoubleOrNull() ?: 0.0) > 0.0
        ) {
            Text("Save Expense")
        }
    }
}
