package com.example.pbdassignment.feature.category.data

import com.example.pbdassignment.feature.category.domain.Category
import com.example.pbdassignment.feature.category.domain.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomCategoryRepository(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun observeCategories(): Flow<List<Category>> {
        return categoryDao.observeCategories().map { categories ->
            categories.map { it.toCategory() }
        }
    }

    override suspend fun addCategory(category: Category) {
        categoryDao.insertCategory(category.toCategoryEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toCategoryEntity())
    }

    override suspend fun deleteCategory(id: String) {
        categoryDao.deleteCategory(id)
    }

    override suspend fun insertDefaultCategories(categories: List<Category>) {
        categories.forEach { category ->
            categoryDao.insertDefaultCategory(category.toCategoryEntity())
        }
    }
}