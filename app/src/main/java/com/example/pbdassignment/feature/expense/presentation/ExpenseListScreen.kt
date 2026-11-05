package com.example.pbdassignment.feature.expense.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pbdassignment.feature.expense.components.ExpenseCard
import com.example.pbdassignment.feature.expense.domain.Expense

@Composable
fun ExpenseListScreen(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier,
    onAddExpenseClick: () -> Unit = {},
    onEditExpenseClick: (Expense) -> Unit = {}
) {
    val expenses by viewModel.expenses.collectAsState()
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val categories = remember(expenses) {
        expenses.map { it.categoryName }.distinct().sorted()
    }

    val filteredExpenses = remember(expenses, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) expenses
        else expenses.filter { it.categoryName.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Category Filter Chips (Minimalist)
        if (categories.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = { selectedCategoryFilter = null },
                    label = { Text("All (${expenses.size})", fontSize = 12.sp) },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1F2937),
                        selectedLabelColor = Color.White,
                        containerColor = Color.Transparent,
                        labelColor = Color(0xFF4B5563)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedCategoryFilter == null) Color(0xFF1F2937) else Color(0xFFE5E7EB)
                    )
                )

                categories.forEach { category ->
                    val isSelected = selectedCategoryFilter == category
                    val count = expenses.count { it.categoryName == category }
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategoryFilter = if (isSelected) null else category
                        },
                        label = { Text("$category ($count)", fontSize = 12.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1F2937),
                            selectedLabelColor = Color.White,
                            containerColor = Color.Transparent,
                            labelColor = Color(0xFF4B5563)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF1F2937) else Color(0xFFE5E7EB)
                        )
                    )
                }
            }
        }

        // Expenses List
        if (filteredExpenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No expenses recorded",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280)
                    )
                    OutlinedButton(
                        onClick = onAddExpenseClick,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFD1D5DB))
                    ) {
                        Text("Add Expense", color = Color(0xFF111827), fontSize = 13.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = filteredExpenses,
                    key = { it.id }
                ) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onDeleteClick = { expenseId ->
                            viewModel.deleteExpense(expenseId)
                        },
                        onEditClick = { selectedExpense ->
                            onEditExpenseClick(selectedExpense)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
