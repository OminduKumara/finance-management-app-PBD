package com.example.pbdassignment.feature.category.domain

data class Category(
    val id: String,
    val name: String,
    val icon: String = "📁",
    val color: Long = 0xFF607D8B,
    val isDefault: Boolean = false
)