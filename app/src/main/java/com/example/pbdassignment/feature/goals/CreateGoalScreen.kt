package com.example.pbdassignment.feature.goals

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pbdassignment.feature.goals.components.CreateGoalDialog

@Composable
fun CreateGoalScreen(
    viewModel: GoalViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    CreateGoalDialog(
        onDismiss = onNavigateBack,
        onConfirm = { name, targetAmount, months, initialDeposit, note ->
            viewModel.createGoal(
                name = name,
                targetAmount = targetAmount,
                months = months,
                initialDeposit = initialDeposit,
                note = note,
                onSuccess = onNavigateBack
            )
        }
    )
}
