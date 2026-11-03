package com.example.pbd.feature_dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pbd.ui.theme.CommittedBlue
import com.example.pbd.ui.theme.DiscretionaryOrange
import com.example.pbd.ui.theme.GoalGold

@Composable
fun SpendingRatioCard(
    committedRatio: Float,
    discretionaryRatio: Float,
    savingsRatio: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "50 / 30 / 20 Financial Balance Meter",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Committed (Fixed) vs Discretionary (Lifestyle) vs Goal Savings",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-segment ratio bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(RoundedCornerShape(9.dp))
            ) {
                if (committedRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(committedRatio.coerceAtLeast(0.05f))
                            .fillMaxHeight()
                            .background(CommittedBlue)
                    )
                }
                if (discretionaryRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(discretionaryRatio.coerceAtLeast(0.05f))
                            .fillMaxHeight()
                            .background(DiscretionaryOrange)
                    )
                }
                if (savingsRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(savingsRatio.coerceAtLeast(0.05f))
                            .fillMaxHeight()
                            .background(GoalGold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendItem(
                    color = CommittedBlue,
                    label = "Committed",
                    percentage = (committedRatio * 100).toInt()
                )
                LegendItem(
                    color = DiscretionaryOrange,
                    label = "Discretionary",
                    percentage = (discretionaryRatio * 100).toInt()
                )
                LegendItem(
                    color = GoalGold,
                    label = "MacBook Save",
                    percentage = (savingsRatio * 100).toInt()
                )
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, percentage: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = "$label: $percentage%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}
