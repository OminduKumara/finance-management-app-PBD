package com.example.pbdassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold

import androidx.compose.ui.Modifier

import com.example.pbdassignment.ui.theme.PbdassignmentTheme

import com.example.pbdassignment.feature.expense.presentation.ExpenseListScreen

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pbdassignment.feature.expense.data.FakeExpenseRepository
import com.example.pbdassignment.feature.expense.presentation.ExpenseViewModel
import com.example.pbdassignment.feature.expense.presentation.ExpenseViewModelFactory

import androidx.compose.runtime.remember

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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ExpenseListScreen(
                        viewModel = expenseViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

            }
        }
    }
}
