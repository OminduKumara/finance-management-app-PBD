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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pbdassignment.feature.income.domain.CryptoTradeRecord
import com.example.pbdassignment.feature.income.presentation.IncomeUiEvent
import com.example.pbdassignment.feature.income.presentation.IncomeUiState
import com.example.pbdassignment.ui.theme.CryptoPurple
import com.example.pbdassignment.ui.theme.IncomeGreen
import com.example.pbdassignment.ui.theme.OverdueRed
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoPnLScreen(
    state: IncomeUiState,
    onEvent: (IncomeUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val fmt = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Crypto P&L — Binance", fontWeight = FontWeight.ExtraBold)
                        Text(
                            "Realised gains/losses on ETH, BTC, SOL",
                            style = MaterialTheme.typography.bodySmall,
                            color = CryptoPurple
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(IncomeUiEvent.OpenCryptoSheet) },
                containerColor = CryptoPurple,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Trade", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }
            // Portfolio total card
            item {
                val totalPnL = state.cryptoTrades.sumOf { it.realisedPnLLkr }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (totalPnL >= 0) Color(0xFF1A0A2E) else Color(0xFF2E0A0A)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Portfolio Realised P&L", style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(0.7f))
                        Spacer(Modifier.height(6.dp))
                        val sign = if (totalPnL >= 0) "+" else ""
                        Text(
                            "${sign}LKR ${fmt.format(totalPnL)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (totalPnL >= 0) IncomeGreen else OverdueRed
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("${state.cryptoTrades.size} trade(s) recorded via Binance",
                            style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.5f))
                    }
                }
            }
            if (state.cryptoTrades.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), Alignment.Center) {
                        Text("No crypto trades yet. Tap + to record a Binance trade.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(state.cryptoTrades, key = { it.id }) { trade ->
                    CryptoTradeCard(trade, fmt)
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
    // Bottom sheet for adding a trade
    if (state.showCryptoSheet) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(IncomeUiEvent.DismissCryptoSheet) },
            sheetState = sheetState
        ) {
            CryptoTradeForm(state, onEvent)
        }
    }
}
@Composable
private fun CryptoTradeCard(trade: CryptoTradeRecord, fmt: NumberFormat) {
    val pnl = trade.realisedPnLLkr
    val isProfit = pnl >= 0
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(trade.dateTimestamp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        if (isProfit) Color(0xFF1A0A2E) else Color(0xFF2E0A0A),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    trade.assetSymbol.take(3),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isProfit) IncomeGreen else OverdueRed
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("${trade.assetSymbol} Trade", fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium)
                Text("Buy \$${trade.buyPriceUsd} → Sell \$${trade.sellPriceUsd}  ×${trade.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${trade.exchange} • $dateStr", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                val sign = if (isProfit) "+" else ""
                Text("${sign}LKR ${fmt.format(pnl)}",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isProfit) IncomeGreen else OverdueRed)
                Text("${if (trade.realisedPnLUsd >= 0) "+" else ""}$ ${fmt.format(trade.realisedPnLUsd)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
@Composable
private fun CryptoTradeForm(state: IncomeUiState, onEvent: (IncomeUiEvent) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Record Binance Trade", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        OutlinedTextField(
            value = state.formAssetSymbol,
            onValueChange = { onEvent(IncomeUiEvent.FormAssetSymbolChanged(it)) },
            label = { Text("Asset Symbol") },
            placeholder = { Text("ETH, BTC, SOL") },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = state.formBuyPriceUsd,
                onValueChange = { onEvent(IncomeUiEvent.FormBuyPriceChanged(it)) },
                label = { Text("Buy Price (USD)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true, modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.formSellPriceUsd,
                onValueChange = { onEvent(IncomeUiEvent.FormSellPriceChanged(it)) },
                label = { Text("Sell Price (USD)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true, modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = state.formQuantity,
                onValueChange = { onEvent(IncomeUiEvent.FormQuantityChanged(it)) },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true, modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.formCryptoExchangeRate,
                onValueChange = { onEvent(IncomeUiEvent.FormCryptoRateChanged(it)) },
                label = { Text("USD → LKR") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true, modifier = Modifier.weight(1f)
            )
        }
        if (state.formPnLPreview.isNotBlank()) {
            Text(state.formPnLPreview, style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (state.formPnLPreview.contains("+")) IncomeGreen else OverdueRed)
        }
        if (state.errorMessage != null) {
            Text(state.errorMessage, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }
        Button(
            onClick = { onEvent(IncomeUiEvent.SubmitCryptoTrade) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CryptoPurple)
        ) {
            Text("Record Trade", fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(Modifier.height(16.dp))
    }
}