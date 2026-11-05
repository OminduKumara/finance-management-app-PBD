package com.example.pbdassignment.feature.income

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pbdassignment.feature.income.components.PendingBanner
import com.example.pbdassignment.feature.income.components.SummaryCard
import com.example.pbdassignment.feature.income.domain.IncomeRecord
import com.example.pbdassignment.feature.income.domain.IncomeSource
import com.example.pbdassignment.feature.income.domain.MilestoneStatus
import com.example.pbdassignment.feature.income.presentation.IncomeUiEvent
import com.example.pbdassignment.feature.income.presentation.IncomeUiState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.pbdassignment.ui.theme.CryptoPurple
import com.example.pbdassignment.ui.theme.IncomeGreen
import com.example.pbdassignment.ui.theme.OverdueRed



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeDashboardScreen(
    state: IncomeUiState,
    onEvent: (IncomeUiEvent) -> Unit,
    onNavigateToAddIncome: () -> Unit,
    onNavigateToCryptoPnL: () -> Unit
) {
    val fmt = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Income & Inflows", fontWeight = FontWeight.ExtraBold)
                        Text(
                            "Kavindu's 4-stream income tracker",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCryptoPnL) {
                        Icon(Icons.Default.TrendingUp, contentDescription = "Crypto P&L")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddIncome,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Income")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item {
                SummaryCard(
                    totalLkr = state.totalReceivedLkr,
                    salaryLkr = state.salarySumLkr,
                    freelanceLkr = state.freelanceSumLkr,
                    adsenseLkr = state.adsenseSumLkr,
                    cryptoPnLLkr = state.cryptoPnLLkr,
                    fmt = fmt
                )
            }

            if (state.pendingFreelanceCount > 0) {
                item {
                    PendingBanner(
                        count = state.pendingFreelanceCount,
                        totalLkr = state.pendingFreelanceLkr,
                        fmt = fmt
                    )
                }
            }
            item {
                Text(
                    "All Records",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (state.incomeList.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), Alignment.Center) {
                        Text("No income records yet. Tap + to add.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(state.incomeList, key = { it.id }) { record ->
                    IncomeRecordCard(
                        record = record,
                        fmt = fmt,
                        onDelete = { onEvent(IncomeUiEvent.DeleteIncome(record.id)) }
                    )
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
@Composable
private fun IncomeRecordCard(
    record: IncomeRecord,
    fmt: NumberFormat,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        .format(Date(record.dateTimestamp))
    val dotColor = when (record.source) {
        IncomeSource.SALARY -> IncomeGreen
        IncomeSource.FREELANCE -> Color(0xFF1976D2)
        IncomeSource.ADSENSE -> Color(0xFFF57C00)
        IncomeSource.CRYPTO -> CryptoPurple
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).clip(CircleShape).background(dotColor))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(record.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${record.source.displayName} • $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (record.source == IncomeSource.FREELANCE && record.milestoneStatus != null) {
                    val badgeColor = when (record.milestoneStatus) {
                        MilestoneStatus.RECEIVED -> IncomeGreen
                        MilestoneStatus.INVOICED -> Color(0xFFF57C00)
                        MilestoneStatus.PENDING -> OverdueRed
                    }
                    Text(record.milestoneStatus.displayName, style = MaterialTheme.typography.labelSmall,
                        color = badgeColor, fontWeight = FontWeight.Bold)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("+LKR ${fmt.format(record.convertedAmountLkr)}",
                    fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyMedium,
                    color = dotColor)
                if (record.source == IncomeSource.ADSENSE) {
                    Text("$ ${record.originalAmount}", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(0.7f),
                    modifier = Modifier.size(18.dp))
            }
        }
    }
}