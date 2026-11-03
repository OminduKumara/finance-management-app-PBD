package com.example.pbdassignment.feature.category.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pbdassignment.feature.category.domain.Category
import java.util.UUID
import androidx.compose.material3.TextButton

@Composable
fun CategoryManagementScreen(
    categories: List<Category>,
    onAddCategory: (Category) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onBack: () -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onBack
        ) {
            Text("← Back to Expenses")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            label = { Text("Category name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val name = categoryName.trim()
                if (name.isNotEmpty() &&
                    categories.none { it.name.equals(name, ignoreCase = true) }
                ) {
                    onAddCategory(
                        Category(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            icon = "📁",
                            isDefault = false
                        )
                    )
                    categoryName = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = categoryName.trim().isNotEmpty()
        ) {
            Text("Add Category")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your Categories",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (categories.isEmpty()) {
            Text("No categories yet.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${category.icon} ${category.name}",
                                modifier = Modifier.weight(1f)
                            )

                            if (!category.isDefault) {
                                TextButton(
                                    onClick = {
                                        onDeleteCategory(category.id)
                                    }
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}