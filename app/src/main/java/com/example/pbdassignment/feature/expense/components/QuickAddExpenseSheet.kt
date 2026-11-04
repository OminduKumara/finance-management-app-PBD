package com.example.pbdassignment.feature.expense.components

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pbdassignment.core.model.DefaultExpenseCategories
import com.example.pbdassignment.core.model.ExpenseNature
import com.example.pbdassignment.core.model.PaymentSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddExpenseSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (
        amount: Double,
        category: String,
        nature: ExpenseNature,
        source: PaymentSource,
        preset: String?,
        notes: String
    ) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(DefaultExpenseCategories.FOOD) }
    var customCategoryName by remember { mutableStateOf("") }
    var selectedNature by remember { mutableStateOf(ExpenseNature.DISCRETIONARY) }
    var selectedSource by remember { mutableStateOf(PaymentSource.PRIMARY_CARD) }
    var selectedPreset by remember { mutableStateOf<String?>(null) }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Log Outflow",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount (LKR)") },
                placeholder = { Text("e.g. 1500") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Quick Presets
            Text("Quick Presets", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = {
                        selectedPreset = "PickMe"
                        selectedCategory = DefaultExpenseCategories.TRANSPORT
                        selectedNature = ExpenseNature.DISCRETIONARY
                        selectedSource = PaymentSource.PRIMARY_CARD
                        if (amountText.isEmpty()) amountText = "1250"
                    },
                    label = { Text("🚗 PickMe") }
                )
                SuggestionChip(
                    onClick = {
                        selectedPreset = "UberEats"
                        selectedCategory = DefaultExpenseCategories.FOOD
                        selectedNature = ExpenseNature.DISCRETIONARY
                        selectedSource = PaymentSource.PRIMARY_CARD
                        if (amountText.isEmpty()) amountText = "2400"
                    },
                    label = { Text("🍔 UberEats") }
                )
                SuggestionChip(
                    onClick = {
                        selectedPreset = "Coffee"
                        selectedCategory = DefaultExpenseCategories.FOOD
                        selectedNature = ExpenseNature.DISCRETIONARY
                        selectedSource = PaymentSource.CASH
                        if (amountText.isEmpty()) amountText = "950"
                    },
                    label = { Text("☕ Cafe") }
                )
                SuggestionChip(
                    onClick = {
                        selectedPreset = "Rent"
                        selectedCategory = DefaultExpenseCategories.HOUSING
                        selectedNature = ExpenseNature.COMMITTED
                        selectedSource = PaymentSource.PRIMARY_CARD
                        if (amountText.isEmpty()) amountText = "34000"
                    },
                    label = { Text("🏠 Rent") }
                )
                SuggestionChip(
                    onClick = {
                        selectedPreset = "Gym"
                        selectedCategory = DefaultExpenseCategories.HEALTH_GYM
                        selectedNature = ExpenseNature.COMMITTED
                        selectedSource = PaymentSource.SECONDARY_ACCOUNT
                        if (amountText.isEmpty()) amountText = "4500"
                    },
                    label = { Text("🏋️ Gym Auto-Debit") }
                )
            }

            // Categories
            Text("Category / Stream Name", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DefaultExpenseCategories.list.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = cat
                            selectedPreset = null
                        },
                        label = { Text(cat) }
                    )
                }
            }

            if (selectedCategory == DefaultExpenseCategories.OTHER) {
                OutlinedTextField(
                    value = customCategoryName,
                    onValueChange = { customCategoryName = it },
                    label = { Text("Custom Outflow Name") },
                    placeholder = { Text("e.g. Car Insurance, Pet Care, Course Fee") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Nature: Committed vs Discretionary
            Text("Classification", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedNature == ExpenseNature.COMMITTED,
                    onClick = { selectedNature = ExpenseNature.COMMITTED },
                    label = { Text("Fixed (Committed)") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedNature == ExpenseNature.DISCRETIONARY,
                    onClick = { selectedNature = ExpenseNature.DISCRETIONARY },
                    label = { Text("Lifestyle (Discretionary)") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Payment Source
            Text("Payment Channel", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentSource.values().forEach { source ->
                    FilterChip(
                        selected = selectedSource == source,
                        onClick = { selectedSource = source },
                        label = { Text(source.label) }
                    )
                }
            }

            // Optional note
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Note / Description") },
                placeholder = { Text("e.g. Lunch with client, Electric bill") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    val finalCategory = if (selectedCategory == DefaultExpenseCategories.OTHER) {
                        customCategoryName.ifBlank { "Custom Outflow" }
                    } else {
                        selectedCategory
                    }

                    if (amount == null || amount <= 0) {
                        errorMessage = "Please enter a valid positive amount"
                    } else {
                        onConfirm(
                            amount,
                            finalCategory,
                            selectedNature,
                            selectedSource,
                            selectedPreset,
                            notes.ifBlank { selectedPreset ?: finalCategory }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Log Outflow", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
