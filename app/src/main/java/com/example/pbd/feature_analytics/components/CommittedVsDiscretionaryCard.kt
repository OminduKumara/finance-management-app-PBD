package com.example.pbd.feature_analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pbd.ui.theme.CommittedBlue
import com.example.pbd.ui.theme.DiscretionaryOrange
import java.text.NumberFormat

@Composable
fun CommittedVsDiscretionaryCard(
    committedExpenseLkr: Double,
    discretionaryExpenseLkr: Double,
    committedRatio: Float,
    discretionaryRatio: Float,
    currencyFormatter: NumberFormat,
    modifier: Modifier = Modifier
) {
    val committedPercent = (committedRatio * 100).toInt()
    val discretionaryPercent = (discretionaryRatio * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Committed vs. Discretionary Spending",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Unavoidable fixed costs vs. flexible lifestyle choices",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Progress Split Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (committedRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(committedRatio.coerceAtLeast(0.01f))
                            .height(16.dp)
                            .background(CommittedBlue)
                    )
                }
                if (discretionaryRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(discretionaryRatio.coerceAtLeast(0.01f))
                            .height(16.dp)
                            .background(DiscretionaryOrange)
                    )
                }
                if (committedRatio <= 0f && discretionaryRatio <= 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Committed Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(CommittedBlue)
                        )
                        Text(
                            text = " Committed ($committedPercent%)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LKR ${currencyFormatter.format(committedExpenseLkr)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CommittedBlue
                    )
                    Text(
                        text = "Rent (LKR 34k), Gym, Utilities",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Discretionary Column
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(DiscretionaryOrange)
                        )
                        Text(
                            text = " Discretionary ($discretionaryPercent%)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LKR ${currencyFormatter.format(discretionaryExpenseLkr)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DiscretionaryOrange
                    )
                    Text(
                        text = "PickMe, UberEats, Dining",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Behavioral Interceptor Tip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Text(
                    text = "💡 Insight: Discretionary spend is where Kavindu's budget leaks happen. Cutting LKR 10,000 in monthly food deliveries accelerates MacBook savings by ~1 week.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
