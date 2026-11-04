package com.example.pbdassignment.feature.analytics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pbdassignment.feature.analytics.components.CommittedVsDiscretionaryCard
import com.example.pbdassignment.feature.analytics.components.ExpenseCategoryChart
import com.example.pbdassignment.feature.analytics.components.IncomeSourceChart
import com.example.pbdassignment.ui.theme.EmeraldPrimary
import com.example.pbdassignment.ui.theme.ExpenseRed
import com.example.pbdassignment.ui.theme.IncomeGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val currencyFormatter = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "Financial Analytics",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Spending distributions and multi-source cash flow patterns",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Time Period Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.availablePeriods.forEach { period ->
                    FilterChip(
                        selected = uiState.selectedPeriod.label == period.label,
                        onClick = { viewModel.selectPeriod(period) },
                        label = { Text(period.label) }
                    )
                }
            }
        }

        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            val snapshot = uiState.snapshot

            // Period Summary Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Summary: ${uiState.selectedPeriod.label}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Inflow", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "+LKR ${currencyFormatter.format(snapshot.totalIncomeLkr)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IncomeGreen
                                )
                            }

                            Column {
                                Text("Total Outflow", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "-LKR ${currencyFormatter.format(snapshot.totalExpenseLkr)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Savings Rate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                val ratePercent = (snapshot.savingsRate * 100).toInt()
                                Text(
                                    text = "$ratePercent%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (snapshot.savingsRate >= 0) EmeraldPrimary else ExpenseRed
                                )
                            }
                        }
                    }
                }
            }

            // 1. Committed vs. Discretionary Spending Split
            item {
                CommittedVsDiscretionaryCard(
                    committedExpenseLkr = snapshot.committedExpenseLkr,
                    discretionaryExpenseLkr = snapshot.discretionaryExpenseLkr,
                    committedRatio = snapshot.committedRatio,
                    discretionaryRatio = snapshot.discretionaryRatio,
                    currencyFormatter = currencyFormatter
                )
            }

            // 2. Expense Distribution by Category
            item {
                ExpenseCategoryChart(
                    categories = snapshot.expenseByCategory,
                    totalExpenseLkr = snapshot.totalExpenseLkr,
                    currencyFormatter = currencyFormatter
                )
            }

            // 3. Multi-Source Income Breakdown
            item {
                IncomeSourceChart(
                    incomeSources = snapshot.incomeBySource,
                    totalIncomeLkr = snapshot.totalIncomeLkr,
                    currencyFormatter = currencyFormatter
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
