
package com.example.pbdassignment.feature.expense.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pbdassignment.feature.expense.domain.Expense
import java.text.NumberFormat
import java.util.Locale

import java.text.SimpleDateFormat
import java.util.Date

import androidx.compose.material3.Button

@Composable
fun ExpenseCard(
    expense: Expense,
    modifier: Modifier = Modifier,
    onDeleteClick: (String) -> Unit = {},
    onEditClick: (Expense) -> Unit = {}
){
    val formattedDate = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    ).format(Date(expense.dateMillis))

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = expense.title,
                style = MaterialTheme.typography.titleMedium
            )

            val formattedAmount = NumberFormat
                .getNumberInstance(Locale.getDefault())
                .format(expense.amount)

            Text("${expense.currencyCode} $formattedAmount")
            Text("Category: ${expense.categoryName}")
            Text("Payment method: ${expense.paymentMethod}")

            Text(
                text = "Date: $formattedDate",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = if (expense.isCommitted) {
                    "Committed expense"
                } else {
                    "Discretionary expense"
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = { onEditClick(expense) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit")
            }

            Button(
                onClick = { onDeleteClick(expense.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete")
            }
        }
    }
}
