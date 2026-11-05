package com.example.pbdassignment.feature.income

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pbdassignment.feature.income.domain.IncomeSource
import com.example.pbdassignment.feature.income.domain.MilestoneStatus
import com.example.pbdassignment.feature.income.presentation.IncomeUiEvent
import com.example.pbdassignment.feature.income.presentation.IncomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeScreen(
    state: IncomeUiState,
    onEvent: (IncomeUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Income", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Source chips
            Text("Income Source", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IncomeSource.values().forEach { src ->
                    FilterChip(
                        selected = state.formSelectedSource == src,
                        onClick = { onEvent(IncomeUiEvent.SelectSource(src)) },
                        label = { Text(src.displayName) }
                    )
                }
            }
            // Title
            OutlinedTextField(
                value = state.formTitle,
                onValueChange = { onEvent(IncomeUiEvent.FormTitleChanged(it)) },
                label = { Text("Income Title") },
                isError = state.formTitleError != null,
                supportingText = { state.formTitleError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            // Salary fields
            OutlinedTextField(
                value = state.formBaseSalaryLkr,
                onValueChange = { onEvent(IncomeUiEvent.FormBaseSalaryChanged(it)) },
                label = { Text("Base Salary (LKR)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.formBonusLkr,
                onValueChange = { onEvent(IncomeUiEvent.FormBonusChanged(it)) },
                label = { Text("Bonus (LKR)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            // Freelance fields
            OutlinedTextField(
                value = state.formClientName,
                onValueChange = { onEvent(IncomeUiEvent.FormClientNameChanged(it)) },
                label = { Text("Client Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.formInvoiceNumber,
                onValueChange = { onEvent(IncomeUiEvent.FormInvoiceNumberChanged(it)) },
                label = { Text("Invoice Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.formFreelanceAmountLkr,
                onValueChange = { onEvent(IncomeUiEvent.FormFreelanceAmountChanged(it)) },
                label = { Text("Freelance Amount (LKR)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text("Milestone Status", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MilestoneStatus.values().forEach { ms ->
                    FilterChip(
                        selected = state.formMilestoneStatus == ms,
                        onClick = { onEvent(IncomeUiEvent.FormMilestoneStatusChanged(ms)) },
                        label = { Text(ms.displayName) }
                    )
                }
            }
            // AdSense fields
            OutlinedTextField(
                value = state.formUsdAmount,
                onValueChange = { onEvent(IncomeUiEvent.FormUsdAmountChanged(it)) },
                label = { Text("USD Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.formExchangeRate,
                onValueChange = { onEvent(IncomeUiEvent.FormExchangeRateChanged(it)) },
                label = { Text("USD → LKR Rate") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.formLkrPreview.isNotBlank()) {
                Text(state.formLkrPreview, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary)
            }
            // Note
            OutlinedTextField(
                value = state.formNote,
                onValueChange = { onEvent(IncomeUiEvent.FormNoteChanged(it)) },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onEvent(IncomeUiEvent.SubmitIncome) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Income")
            }
            if (state.errorMessage != null) {
                Text(state.errorMessage, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}