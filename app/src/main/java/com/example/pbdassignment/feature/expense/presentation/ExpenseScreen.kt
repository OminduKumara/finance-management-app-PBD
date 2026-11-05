package com.example.pbdassignment.feature.expense.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pbdassignment.feature.category.data.RoomCategoryRepository
import com.example.pbdassignment.feature.category.presentation.CategoryManagementScreen
import com.example.pbdassignment.feature.category.presentation.CategoryViewModel
import com.example.pbdassignment.feature.category.presentation.CategoryViewModelFactory
import com.example.pbdassignment.feature.expense.data.AppDatabase
import com.example.pbdassignment.feature.expense.data.RoomExpenseRepository
import com.example.pbdassignment.feature.expense.domain.Expense
import com.example.pbdassignment.feature.recurring.data.RecurringExpenseGenerator
import com.example.pbdassignment.feature.recurring.data.RoomRecurringExpenseRepository
import com.example.pbdassignment.feature.recurring.domain.RecurringExpense
import com.example.pbdassignment.feature.recurring.presentation.AddRecurringExpenseScreen
import com.example.pbdassignment.feature.recurring.presentation.RecurringExpenseListScreen
import com.example.pbdassignment.feature.recurring.presentation.RecurringExpenseViewModel
import com.example.pbdassignment.feature.recurring.presentation.RecurringExpenseViewModelFactory
import java.text.NumberFormat
import java.util.Locale
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
        ) {
            // Top Clean App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Expenses",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    fontSize = 22.sp
                )

                Button(
                    onClick = {
                        if (selectedTabIndex == 1) {
                            editingRecurringExpense = null
                            activeModalView = "add_recurring"
                        } else {
                            expenseToEdit = null
                            activeModalView = "add_expense"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedTabIndex == 1) "Add Recurring" else "Add Expense",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Minimalist Total Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "TOTAL SPENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B7280),
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "LKR ${currencyFormatter.format(totalOutflows)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Fixed: LKR ${currencyFormatter.format(committedOutflows)}   •   Lifestyle: LKR ${currencyFormatter.format(discretionaryOutflows)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Professional Segmented Control Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE5E7EB))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabTitles = listOf(
                    "Expenses (${expenses.size})",
                    "Recurring (${recurringExpenses.size})",
                    "Categories (${categories.size})"
                )

                tabTitles.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedTabIndex = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF111827) else Color(0xFF6B7280)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
