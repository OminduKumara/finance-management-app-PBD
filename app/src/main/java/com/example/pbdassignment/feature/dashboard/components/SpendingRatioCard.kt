package com.example.pbdassignment.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.sp
import com.example.pbdassignment.ui.theme.CommittedBlue
import com.example.pbdassignment.ui.theme.DiscretionaryOrange
import com.example.pbdassignment.ui.theme.GoalGold

@Composable
fun SpendingRatioCard(
    committedRatio: Float,
    discretionaryRatio: Float,
    savingsRatio: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "50 / 30 / 20 FINANCIAL BALANCE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Needs (50%) • Wants (30%) • Savings (20%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-segment ratio bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (committedRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(committedRatio.coerceAtLeast(0.01f))
                            .fillMaxHeight()
                            .background(CommittedBlue)
                    )
                }
                if (discretionaryRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(discretionaryRatio.coerceAtLeast(0.01f))
                            .fillMaxHeight()
                            .background(DiscretionaryOrange)
                    )
                }
                if (savingsRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(savingsRatio.coerceAtLeast(0.01f))
                            .fillMaxHeight()
                            .background(GoalGold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Column Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendColumn(
                    color = CommittedBlue,
                    label = "Committed",
                    percentage = (committedRatio * 100).toInt()
                )
                LegendColumn(
                    color = DiscretionaryOrange,
                    label = "Discretionary",
                    percentage = (discretionaryRatio * 100).toInt()
                )
                LegendColumn(
                    color = GoalGold,
                    label = "Goal Savings",
                    percentage = (savingsRatio * 100).toInt()
                )
            }
        }
    }
}

@Composable
private fun LegendColumn(
    color: Color,
    label: String,
    percentage: Int
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = " $label",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
