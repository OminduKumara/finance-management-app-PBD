package com.example.pbdassignment.feature.expense.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.example.pbdassignment.ui.theme.CommittedBlue
import com.example.pbdassignment.ui.theme.DiscretionaryOrange
import com.example.pbdassignment.ui.theme.ExpenseRed
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
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

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var activeModalView by remember { mutableStateOf<String?>(null) } // "add_expense", "add_recurring"
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }
    var editingRecurringExpense by remember { mutableStateOf<RecurringExpense?>(null) }

    val expenses by expenseViewModel.expenses.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val recurringExpenses by recurringViewModel.recurringExpenses.collectAsState()

    val currencyFormatter = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    val totalOutflows = remember(expenses) { expenses.sumOf { it.amount } }
    val committedOutflows = remember(expenses) { expenses.filter { it.isCommitted }.sumOf { it.amount } }
    val discretionaryOutflows = remember(expenses) { expenses.filter { !it.isCommitted }.sumOf { it.amount } }

    if (activeModalView == "add_expense") {
        QuickAddExpenseScreen(
            initialExpense = expenseToEdit,
            categories = categories,
            onSave = { title, amount, categoryName, paymentMethod, isCommitted ->
                if (expenseToEdit != null) {
                    expenseViewModel.updateExpense(
                        expenseToEdit!!.copy(
                            title = title,
                            amount = amount,
                            categoryId = categories.firstOrNull { it.name == categoryName }?.id ?: categoryName.lowercase(),
                            categoryName = categoryName,
                            paymentMethod = paymentMethod,
                            isCommitted = isCommitted
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
                        isCommitted = isCommitted,
                        paymentMethod = paymentMethod
                    )
                    expenseViewModel.addExpense(newExpense)
                }
                expenseToEdit = null
                activeModalView = null
            },
            onCancel = {
                expenseToEdit = null
                activeModalView = null
            }
        )
    } else if (activeModalView == "add_recurring") {
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
                activeModalView = null
            },
            onCancel = {
                editingRecurringExpense = null
                activeModalView = null
            }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                if (selectedTabIndex == 0) {
                    FloatingActionButton(
                        onClick = {
                            expenseToEdit = null
                            activeModalView = "add_expense"
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Outflow")
                    }
                } else if (selectedTabIndex == 1) {
                    FloatingActionButton(
                        onClick = {
                            editingRecurringExpense = null
                            activeModalView = "add_recurring"
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Recurring Expense")
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Outflows & Expenses",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Track committed bills, PickMe rides & lifestyle leaks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Summary Metrics Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total Outflows",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "LKR ${currencyFormatter.format(totalOutflows)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = ExpenseRed
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Committed (Fixed)", style = MaterialTheme.typography.labelSmall, color = CommittedBlue)
                                Text(
                                    "LKR ${currencyFormatter.format(committedOutflows)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Lifestyle (Discretionary)", style = MaterialTheme.typography.labelSmall, color = DiscretionaryOrange)
                                Text(
                                    "LKR ${currencyFormatter.format(discretionaryOutflows)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Expenses (${expenses.size})") },
                        icon = { Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Recurring (${recurringExpenses.size})") },
                        icon = { Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("Categories (${categories.size})") },
                        icon = { Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tab Content
                when (selectedTabIndex) {
                    0 -> {
                        ExpenseListScreen(
                            viewModel = expenseViewModel,
                            onAddExpenseClick = {
                                expenseToEdit = null
                                activeModalView = "add_expense"
                            },
                            onEditExpenseClick = { selectedExpense ->
                                expenseToEdit = selectedExpense
                                activeModalView = "add_expense"
                            }
                        )
                    }
                    1 -> {
                        RecurringExpenseListScreen(
                            expenses = recurringExpenses,
                            onAddClick = {
                                editingRecurringExpense = null
                                activeModalView = "add_recurring"
                            },
                            onEditClick = { recurring ->
                                editingRecurringExpense = recurring
                                activeModalView = "add_recurring"
                            },
                            onDeleteClick = { id ->
                                recurringViewModel.deleteRecurringExpense(id)
                            },
                            onBack = {
                                selectedTabIndex = 0
                            }
                        )
                    }
                    2 -> {
                        CategoryManagementScreen(
                            categories = categories,
                            onAddCategory = { category ->
                                categoryViewModel.addCategory(category)
                            },
                            onDeleteCategory = { categoryId ->
                                categoryViewModel.deleteCategory(categoryId)
                            },
                            onBack = {
                                selectedTabIndex = 0
                            }
                        )
                    }
                }
            }
        }
    }
}
