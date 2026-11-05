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
import com.example.pbdassignment.core.navigation.NavGraph
import com.example.pbdassignment.feature.category.data.RoomCategoryRepository
import com.example.pbdassignment.feature.category.presentation.CategoryViewModel
import com.example.pbdassignment.feature.category.presentation.CategoryViewModelFactory
import com.example.pbdassignment.feature.expense.data.AppDatabase
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

                LaunchedEffect(Unit) {
                    categoryViewModel.insertDefaultCategories()
                }

                LaunchedEffect(Unit) {
                    recurringExpenseGenerator.generateDueExpenses()
                }

                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
