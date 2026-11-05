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
            com.example.pbdassignment.core.firebase.FirebaseManager.syncCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
            com.example.pbdassignment.core.firebase.FirebaseManager.syncCategory(category)
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCategory(id)
            com.example.pbdassignment.core.firebase.FirebaseManager.deleteCategory(id)
        }
    }

    fun insertDefaultCategories() {
        viewModelScope.launch {
            val defaults = com.example.pbdassignment.feature.category.data.DefaultCategories.categories
            repository.insertDefaultCategories(defaults)
            defaults.forEach {
                com.example.pbdassignment.core.firebase.FirebaseManager.syncCategory(it)
            }
        }
    }
}