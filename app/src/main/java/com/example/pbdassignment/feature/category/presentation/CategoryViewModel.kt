package com.example.pbdassignment.feature.category.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pbdassignment.feature.category.domain.Category
import com.example.pbdassignment.feature.category.domain.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    val categories = repository.observeCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCategory(category: Category) {
        viewModelScope.launch {
            repository.addCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCategory(id)
        }
    }

    fun insertDefaultCategories() {
        viewModelScope.launch {
            repository.insertDefaultCategories(
                com.example.pbdassignment.feature.category.data.DefaultCategories.categories
            )
        }
    }
}