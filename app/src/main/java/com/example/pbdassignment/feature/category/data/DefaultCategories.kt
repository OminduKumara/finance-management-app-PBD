package com.example.pbdassignment.feature.category.data

import com.example.pbdassignment.feature.category.domain.Category

object DefaultCategories {

    val categories = listOf(
        Category(
            id = "food",
            name = "Food",
            icon = "🍔",
            isDefault = true
        ),
        Category(
            id = "transport",
            name = "Transport",
            icon = "🚗",
            isDefault = true
        ),
        Category(
            id = "shopping",
            name = "Shopping",
            icon = "🛍️",
            isDefault = true
        ),
        Category(
            id = "bills",
            name = "Bills",
            icon = "🧾",
            isDefault = true
        ),
        Category(
            id = "entertainment",
            name = "Entertainment",
            icon = "🎬",
            isDefault = true
        ),
        Category(
            id = "health",
            name = "Health",
            icon = "🏥",
            isDefault = true
        ),
        Category(
            id = "other",
            name = "Other",
            icon = "📁",
            isDefault = true
        )
    )
}