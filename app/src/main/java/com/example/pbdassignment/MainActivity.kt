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
import androidx.room.Room
import com.example.pbdassignment.feature.expense.data.AppDatabase
import com.example.pbdassignment.feature.expense.data.RoomExpenseRepository
import com.example.pbdassignment.feature.expense.presentation.ExpenseListScreen
import com.example.pbdassignment.feature.expense.presentation.ExpenseViewModel
import com.example.pbdassignment.feature.expense.presentation.ExpenseViewModelFactory
import com.example.pbdassignment.feature.expense.presentation.QuickAddExpenseScreen
import com.example.pbdassignment.ui.theme.PbdassignmentTheme
import com.example.pbdassignment.feature.category.presentation.CategoryViewModel
import com.example.pbdassignment.feature.category.presentation.CategoryViewModelFactory
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.example.pbdassignment.feature.category.presentation.CategoryManagementScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PbdassignmentTheme {
                val database = remember {
                    Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "pbd_database"
                    )
                        .addMigrations(AppDatabase.MIGRATION_1_2)
                        .build()
                }

                val repository = remember {
                    RoomExpenseRepository(database.expenseDao())
                }

                val categoryRepository = remember {
                    com.example.pbdassignment.feature.category.data.RoomCategoryRepository(
                        database.categoryDao()
                    )
                }

                val expenseViewModel: ExpenseViewModel = viewModel(
                    factory = ExpenseViewModelFactory(repository)
                )

                val categoryViewModel: CategoryViewModel = viewModel(
                    factory = CategoryViewModelFactory(categoryRepository)
                )

                LaunchedEffect(Unit) {
                    categoryViewModel.insertDefaultCategories()
                }

                var showCategoryManagement by remember { mutableStateOf(false) }
                var showAddExpense by remember { mutableStateOf(false) }

                var expenseToEdit by remember {
                    mutableStateOf<com.example.pbdassignment.feature.expense.domain.Expense?>(null)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (showAddExpense) {
                        QuickAddExpenseScreen(
                            modifier = Modifier.padding(innerPadding),
                            categories = categoryViewModel.categories.collectAsState().value,
                            initialExpense = expenseToEdit,

                            onSave = { title, amount, categoryName, paymentMethod ->
                                val existingExpense = expenseToEdit

                                if (existingExpense != null) {
                                    expenseViewModel.updateExpense(
                                        existingExpense.copy(
                                            title = title,
                                            amount = amount,
                                            categoryId = categoryViewModel.categories.value
                                                .firstOrNull { it.name == categoryName }
                                                ?.id ?: categoryName.lowercase(),
                                            categoryName = categoryName,
                                            paymentMethod = paymentMethod
                                        )
                                    )
                                } else {
                                    val expense = com.example.pbdassignment.feature.expense.domain.Expense(
                                        id = java.util.UUID.randomUUID().toString(),
                                        title = title,
                                        amount = amount,
                                        categoryId = categoryViewModel.categories.value
                                            .firstOrNull { it.name == categoryName }
                                            ?.id ?: categoryName.lowercase(),
                                        categoryName = categoryName,
                                        dateMillis = System.currentTimeMillis(),
                                        isCommitted = false,
                                        paymentMethod = paymentMethod
                                    )

                                    expenseViewModel.addExpense(expense)
                                }

                                expenseToEdit = null
                                showAddExpense = false
                            },

                            onCancel = {
                                expenseToEdit = null
                                showAddExpense = false
                            }
                        )
                    } else if (showCategoryManagement) {
                        CategoryManagementScreen(
                            categories = categoryViewModel.categories.collectAsState().value,
                            onAddCategory = { category ->
                                categoryViewModel.addCategory(category)
                            },
                            onDeleteCategory = { categoryId ->
                                categoryViewModel.deleteCategory(categoryId)
                            },
                            onBack = {
                                showCategoryManagement = false
                            }
                        )
                    } else {
                        Column(
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            Button(
                                onClick = {
                                    showCategoryManagement = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Manage Categories")
                            }

                            ExpenseListScreen(
                                viewModel = expenseViewModel,
                                modifier = Modifier.weight(1f),
                                onAddExpenseClick = {
                                    expenseToEdit = null
                                    showAddExpense = true
                                },
                                onEditExpenseClick = { selectedExpense ->
                                    expenseToEdit = selectedExpense
                                    showAddExpense = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
