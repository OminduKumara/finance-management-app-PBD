package com.example.pbdassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.pbdassignment.core.data.FinanceRepository
import com.example.pbdassignment.core.model.ExpenseItem
import com.example.pbdassignment.core.model.ExpenseNature
import com.example.pbdassignment.core.model.PaymentSource
import com.example.pbdassignment.core.navigation.NavGraph
import com.example.pbdassignment.feature.category.data.RoomCategoryRepository
import com.example.pbdassignment.feature.category.presentation.CategoryViewModel
import com.example.pbdassignment.feature.category.presentation.CategoryViewModelFactory
import com.example.pbdassignment.feature.expense.data.AppDatabase
import com.example.pbdassignment.feature.expense.data.RoomExpenseRepository
import com.example.pbdassignment.feature.recurring.data.RecurringExpenseGenerator
import com.example.pbdassignment.ui.theme.PbdassignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PbdassignmentTheme {
                val database = remember {
                    AppDatabase.getInstance(applicationContext)
                }

                val categoryRepository = remember {
                    RoomCategoryRepository(database.categoryDao())
                }

                val categoryViewModel: CategoryViewModel = viewModel(
                    factory = CategoryViewModelFactory(categoryRepository)
                )

                val recurringExpenseGenerator = remember {
                    RecurringExpenseGenerator(database)
                }

                val expenseRepository = remember {
                    RoomExpenseRepository(database.expenseDao())
                }

                val incomeRepository = remember {
                    com.example.pbdassignment.feature.income.data.RoomIncomeRepository(database.incomeDao()).also {
                        com.example.pbdassignment.feature.income.data.IncomeRepositoryProvider.instance = it
                    }
                }

                LaunchedEffect(Unit) {
                    categoryViewModel.insertDefaultCategories()
                }

                LaunchedEffect(Unit) {
                    recurringExpenseGenerator.generateDueExpenses()
                }

                LaunchedEffect(Unit) {
                    incomeRepository.observeIncomes().collect { roomIncomes ->
                        roomIncomes.forEach { income ->
                            FinanceRepository.upsertLocalIncome(income)
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    expenseRepository.observeExpenses().collect { roomExpenses ->
                        roomExpenses.forEach { expense ->
                            FinanceRepository.upsertLocalExpense(
                                ExpenseItem(
                                    id = expense.id,
                                    amount = expense.amount,
                                    category = expense.categoryName,
                                    nature = if (expense.isCommitted) ExpenseNature.COMMITTED else ExpenseNature.DISCRETIONARY,
                                    paymentSource = when (expense.paymentMethod.lowercase()) {
                                        "credit card", "credit_card", "card" -> PaymentSource.PRIMARY_CARD
                                        "bank transfer", "debit card" -> PaymentSource.SECONDARY_ACCOUNT
                                        else -> PaymentSource.CASH
                                    },
                                    merchantPreset = expense.title,
                                    notes = expense.title
                                )
                            )
                        }
                    }
                }

                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
