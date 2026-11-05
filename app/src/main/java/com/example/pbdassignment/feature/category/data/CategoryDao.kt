package com.example.pbdassignment.feature.category.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :categoryId AND isDefault = 0")
    suspend fun deleteCategory(categoryId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultCategory(category: CategoryEntity)
}