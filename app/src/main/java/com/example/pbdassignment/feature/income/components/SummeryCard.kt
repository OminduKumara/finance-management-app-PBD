package com.example.pbdassignment.feature.income.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pbdassignment.ui.theme.CryptoPurple
import com.example.pbdassignment.ui.theme.IncomeGreen
import com.example.pbdassignment.ui.theme.OverdueRed
import java.text.NumberFormat

@Composable
fun SummaryCard(
    totalLkr: Double,
    salaryLkr: Double,
    freelanceLkr: Double,
    adsenseLkr: Double,
    cryptoPnLLkr: Double,
    fmt: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Total Received This Month",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "LKR ${fmt.format(totalLkr)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IncomeSourceRow("Salary", salaryLkr, IncomeGreen, fmt)
                IncomeSourceRow("Freelance", freelanceLkr, IncomeGreen, fmt)
                IncomeSourceRow("AdSense", adsenseLkr, IncomeGreen, fmt)
                IncomeSourceRow(
                    "Crypto P&L", cryptoPnLLkr,
                    if (cryptoPnLLkr >= 0) CryptoPurple else OverdueRed,
                    fmt,
                    showSign = true
                )
            }
        }
    }
}