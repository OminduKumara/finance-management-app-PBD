package com.example.pbdassignment.feature.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pbdassignment.feature.category.data.RoomCategoryRepository
import com.example.pbdassignment.feature.category.presentation.CategoryManagementScreen
import com.example.pbdassignment.feature.category.presentation.CategoryViewModel
import com.example.pbdassignment.feature.category.presentation.CategoryViewModelFactory
import com.example.pbdassignment.feature.expense.data.AppDatabase
import com.example.pbdassignment.feature.expense.data.RoomExpenseRepository
import com.example.pbdassignment.feature.expense.domain.Expense
import com.example.pbdassignment.feature.expense.presentation.ExpenseListScreen
import com.example.pbdassignment.feature.expense.presentation.ExpenseViewModel
import com.example.pbdassignment.feature.expense.presentation.ExpenseViewModelFactory
import com.example.pbdassignment.feature.expense.presentation.QuickAddExpenseScreen
import com.example.pbdassignment.feature.recurring.data.RecurringExpenseGenerator
import com.example.pbdassignment.feature.recurring.data.RoomRecurringExpenseRepository
import com.example.pbdassignment.feature.recurring.domain.RecurringExpense
import com.example.pbdassignment.feature.recurring.presentation.AddRecurringExpenseScreen
import com.example.pbdassignment.feature.recurring.presentation.RecurringExpenseListScreen
import com.example.pbdassignment.feature.recurring.presentation.RecurringExpenseViewModel
import com.example.pbdassignment.feature.recurring.presentation.RecurringExpenseViewModelFactory
import java.util.UUID

@Composable
fun ExpenseScreen() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }

    val expenseRepository = remember { RoomExpenseRepository(database.expenseDao()) }
    val categoryRepository = remember { RoomCategoryRepository(database.categoryDao()) }
    val recurringRepository = remember { RoomRecurringExpenseRepository(database.recurringExpenseDao()) }
    val recurringGenerator = remember { RecurringExpenseGenerator(database) }

    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(expenseRepository)
    )
    val categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(categoryRepository)
    )
    val recurringViewModel: RecurringExpenseViewModel = viewModel(
        factory = RecurringExpenseViewModelFactory(recurringRepository)
    )

    LaunchedEffect(Unit) {
        categoryViewModel.insertDefaultCategories()
    }

    LaunchedEffect(Unit) {
        recurringGenerator.generateDueExpenses()
    }

    var currentView by remember { mutableStateOf("list") } // "list", "add_expense", "recurring", "add_recurring", "categories"
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }
    var editingRecurringExpense by remember { mutableStateOf<RecurringExpense?>(null) }

    val categories by categoryViewModel.categories.collectAsState()
    val recurringExpenses by recurringViewModel.recurringExpenses.collectAsState()

    when (currentView) {
        "add_expense" -> {
            QuickAddExpenseScreen(
                initialExpense = expenseToEdit,
                categories = categories,
                onSave = { title, amount, categoryName, paymentMethod ->
                    if (expenseToEdit != null) {
                        expenseViewModel.updateExpense(
                            expenseToEdit!!.copy(
                                title = title,
                                amount = amount,
                                categoryId = categories.firstOrNull { it.name == categoryName }?.id ?: categoryName.lowercase(),
                                categoryName = categoryName,
                                paymentMethod = paymentMethod
                            )
                        )
                    } else {
                        val newExpense = Expense(
                            id = UUID.randomUUID().toString(),
                            title = title,
                            amount = amount,
                            categoryId = categories.firstOrNull { it.name == categoryName }?.id ?: categoryName.lowercase(),
                            categoryName = categoryName,
                            dateMillis = System.currentTimeMillis(),
                            isCommitted = false,
                            paymentMethod = paymentMethod
                        )
                        expenseViewModel.addExpense(newExpense)
                    }
                    expenseToEdit = null
                    currentView = "list"
                },
                onCancel = {
                    expenseToEdit = null
                    currentView = "list"
                }
            )
        }
        "recurring" -> {
            RecurringExpenseListScreen(
                expenses = recurringExpenses,
                onAddClick = {
                    editingRecurringExpense = null
                    currentView = "add_recurring"
                },
                onEditClick = { recurring ->
                    editingRecurringExpense = recurring
                    currentView = "add_recurring"
                },
                onDeleteClick = { id ->
                    recurringViewModel.deleteRecurringExpense(id)
                },
                onBack = {
                    currentView = "list"
                }
            )
        }
        "add_recurring" -> {
            AddRecurringExpenseScreen(
                categories = categories,
                existingExpense = editingRecurringExpense,
                onSave = { recurringExpense ->
                    if (editingRecurringExpense != null) {
                        recurringViewModel.updateRecurringExpense(recurringExpense)
                    } else {
                        recurringViewModel.addRecurringExpense(recurringExpense)
                    }
                    editingRecurringExpense = null
                    currentView = "recurring"
                },
                onCancel = {
                    editingRecurringExpense = null
                    currentView = "recurring"
                }
            )
        }
        "categories" -> {
            CategoryManagementScreen(
                categories = categories,
                onAddCategory = { category ->
                    categoryViewModel.addCategory(category)
                },
                onDeleteCategory = { categoryId ->
                    categoryViewModel.deleteCategory(categoryId)
                },
                onBack = {
                    currentView = "list"
                }
            )
        }
        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { currentView = "recurring" },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Recurring")
                    }
                    OutlinedButton(
                        onClick = { currentView = "categories" },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Categories")
                    }
                }

                ExpenseListScreen(
                    viewModel = expenseViewModel,
                    modifier = Modifier.weight(1f),
                    onAddExpenseClick = {
                        expenseToEdit = null
                        currentView = "add_expense"
                    },
                    onEditExpenseClick = { selected ->
                        expenseToEdit = selected
                        currentView = "add_expense"
                    }
                )
            }
        }
    }
}
