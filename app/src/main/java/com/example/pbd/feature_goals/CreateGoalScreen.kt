package com.example.pbd.feature_goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    viewModel: GoalViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var targetAmountText by remember { mutableStateOf("") }
    var targetMonthsText by remember { mutableStateOf("12") }
    var initialDepositText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Goal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Define your financial milestone",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Goal Title
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = null
                },
                label = { Text("Goal Title *") },
                placeholder = { Text("e.g. MacBook Pro M4 (14-inch)") },
                isError = titleError != null,
                supportingText = titleError?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Target Amount
            OutlinedTextField(
                value = targetAmountText,
                onValueChange = {
                    targetAmountText = it
                    amountError = null
                },
                label = { Text("Target Amount (LKR) *") },
                placeholder = { Text("e.g. 490000") },
                prefix = { Text("LKR ") },
                isError = amountError != null,
                supportingText = amountError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Timeline in Months
            OutlinedTextField(
                value = targetMonthsText,
                onValueChange = { targetMonthsText = it },
                label = { Text("Timeline (Months)") },
                placeholder = { Text("12") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Initial Seed Deposit
            OutlinedTextField(
                value = initialDepositText,
                onValueChange = { initialDepositText = it },
                label = { Text("Initial Deposit (Optional)") },
                placeholder = { Text("e.g. 11200") },
                prefix = { Text("LKR ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Notes
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Purpose or Motivation (Optional)") },
                placeholder = { Text("e.g. Daily freelance driver for modern Android builds") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                        viewModel.createGoal(
                            name = title,
                            targetAmount = targetAmount,
                            months = months,
                            initialDeposit = initialDeposit,
                            note = note,
                            onSuccess = onNavigateBack
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Create Savings Goal", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
