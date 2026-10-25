
package com.example.pbdassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pbdassignment.feature.expense.data.FakeExpenseRepository
import com.example.pbdassignment.feature.expense.presentation.ExpenseListScreen
import com.example.pbdassignment.feature.expense.presentation.ExpenseViewModel
import com.example.pbdassignment.feature.expense.presentation.ExpenseViewModelFactory
import com.example.pbdassignment.feature.expense.presentation.QuickAddExpenseScreen
import com.example.pbdassignment.ui.theme.PbdassignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PbdassignmentTheme {
                val repository = remember { FakeExpenseRepository() }

                val expenseViewModel: ExpenseViewModel = viewModel(
                    factory = ExpenseViewModelFactory(repository)
                )

                var showAddExpense by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (showAddExpense) {
                        QuickAddExpenseScreen(
                            modifier = Modifier.padding(innerPadding),
                            onSave = { title, amount ->
                                val expense = com.example.pbdassignment.feature.expense.domain.Expense(
                                    id = java.util.UUID.randomUUID().toString(),
                                    title = title,
                                    amount = amount,
                                    categoryId = "other",
                                    categoryName = "Other",
                                    dateMillis = System.currentTimeMillis(),
                                    isCommitted = false,
                                    paymentMethod = "Cash"
                                )

                                expenseViewModel.addExpense(expense)
                                showAddExpense = false
                            }
                        )
                    } else {
                        ExpenseListScreen(
                            viewModel = expenseViewModel,
                            modifier = Modifier.padding(innerPadding),
                            onAddExpenseClick = {
                                showAddExpense = true
                            }
                        )
                    }
                }
            }
        }
    }
}
