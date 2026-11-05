package com.example.pbdassignment.feature.category.domain

import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun observeCategories(): Flow<List<Category>>

    suspend fun addCategory(category: Category)

    suspend fun updateCategory(category: Category)

    suspend fun deleteCategory(id: String)

    suspend fun insertDefaultCategories(categories: List<Category>)
}