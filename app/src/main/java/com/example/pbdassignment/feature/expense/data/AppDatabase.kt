package com.example.pbdassignment.feature.expense.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pbdassignment.feature.category.data.CategoryDao
import com.example.pbdassignment.feature.category.data.CategoryEntity

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    abstract fun categoryDao(): CategoryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS categories (
                    id TEXT NOT NULL,
                    name TEXT NOT NULL,
                    icon TEXT NOT NULL,
                    color INTEGER NOT NULL,
                    isDefault INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
                )
            }
        }
    }
}