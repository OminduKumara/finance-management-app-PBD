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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Source selector ───────────────────────────────────────────
            Text("Income Source", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IncomeSource.values().forEach { src ->
                    FilterChip(
                        selected = state.formSelectedSource == src,
                        onClick = { onEvent(IncomeUiEvent.SelectSource(src)) },
                        label = { Text(src.displayName, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
            // ── Shared title field ────────────────────────────────────────
            OutlinedTextField(
                value = state.formTitle,
                onValueChange = { onEvent(IncomeUiEvent.FormTitleChanged(it)) },
                label = { Text("Income Title") },
                placeholder = {
                    Text(when (state.formSelectedSource) {
                        IncomeSource.SALARY -> "e.g. Monthly Salary – Oct"
                        IncomeSource.FREELANCE -> "e.g. Startup React Dashboard"
                        IncomeSource.ADSENSE -> "e.g. Blog AdSense – Oct"
                        IncomeSource.CRYPTO -> "e.g. ETH Swing Trade"
                    })
                },
                isError = state.formTitleError != null,
                supportingText = { state.formTitleError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            // ── Source-specific fields ────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (state.formSelectedSource) {
                        IncomeSource.SALARY -> SalaryFields(state, onEvent)
                        IncomeSource.FREELANCE -> FreelanceFields(state, onEvent)
                        IncomeSource.ADSENSE -> AdSenseFields(state, onEvent)
                        IncomeSource.CRYPTO -> {
                            Text(
                                "Crypto trades are recorded in the Crypto P&L screen.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Tap Save — it will redirect you there.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            // ── Note ──────────────────────────────────────────────────────
            OutlinedTextField(
                value = state.formNote,
                onValueChange = { onEvent(IncomeUiEvent.FormNoteChanged(it)) },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { onEvent(IncomeUiEvent.SubmitIncome) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Income", fontWeight = FontWeight.Bold)
            }
            if (state.errorMessage != null) {
                Text(
                    state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
// ─────────────────────────── Sub-composables ──────────────────────────────
@Composable
private fun SalaryFields(state: IncomeUiState, onEvent: (IncomeUiEvent) -> Unit) {
    Text("Salary Breakdown", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    OutlinedTextField(
        value = state.formBaseSalaryLkr,
        onValueChange = { onEvent(IncomeUiEvent.FormBaseSalaryChanged(it)) },
        label = { Text("Base Salary (LKR)") },
        placeholder = { Text("e.g. 185000") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.formBonusLkr,
        onValueChange = { onEvent(IncomeUiEvent.FormBonusChanged(it)) },
        label = { Text("Performance Bonus (LKR)") },
        placeholder = { Text("0.00") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true, modifier = Modifier.fillMaxWidth()
    )
}
@Composable
private fun FreelanceFields(state: IncomeUiState, onEvent: (IncomeUiEvent) -> Unit) {
    Text("Freelance Invoice", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    OutlinedTextField(
        value = state.formClientName,
        onValueChange = { onEvent(IncomeUiEvent.FormClientNameChanged(it)) },
        label = { Text("Client Name") },
        placeholder = { Text("e.g. Local Startup, SME") },
        singleLine = true, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.formInvoiceNumber,
        onValueChange = { onEvent(IncomeUiEvent.FormInvoiceNumberChanged(it)) },
        label = { Text("Invoice # (optional)") },
        placeholder = { Text("e.g. INV-2024-003") },
        singleLine = true, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.formFreelanceAmountLkr,
        onValueChange = { onEvent(IncomeUiEvent.FormFreelanceAmountChanged(it)) },
        label = { Text("Invoice Amount (LKR)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true, modifier = Modifier.fillMaxWidth()
    )
    Text("Milestone Status", style = MaterialTheme.typography.labelSmall)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MilestoneStatus.values().forEach { ms ->
            FilterChip(
                selected = state.formMilestoneStatus == ms,
                onClick = { onEvent(IncomeUiEvent.FormMilestoneStatusChanged(ms)) },
                label = { Text(ms.displayName) }
            )
        }
    }
}
@Composable
private fun AdSenseFields(state: IncomeUiState, onEvent: (IncomeUiEvent) -> Unit) {
    Text("Google AdSense (USD)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    OutlinedTextField(
        value = state.formUsdAmount,
        onValueChange = { onEvent(IncomeUiEvent.FormUsdAmountChanged(it)) },
        label = { Text("Earnings in USD ($)") },
        placeholder = { Text("e.g. 95.40") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true, modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = state.formExchangeRate,
        onValueChange = { onEvent(IncomeUiEvent.FormExchangeRateChanged(it)) },
        label = { Text("USD → LKR Rate") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true, modifier = Modifier.fillMaxWidth()
    )
    if (state.formLkrPreview.isNotBlank()) {
        Text(
            state.formLkrPreview,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}