package com.example.pbdassignment.feature.income.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.text.NumberFormat

@Composable
fun IncomeSourceRow(
    label: String,
    amountLkr: Double,
    color: Color,
    fmt: NumberFormat,
    showSign: Boolean = false
) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        val sign = if (showSign && amountLkr >= 0) "+" else ""
        Text(
            "${sign}LKR ${fmt.format(amountLkr)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}