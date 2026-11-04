package com.example.pbdassignment.feature.expense.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pbdassignment.feature.category.data.CategoryDao
import com.example.pbdassignment.feature.category.data.CategoryEntity

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pbdassignment.feature.recurring.data.RecurringExpenseDao
import com.example.pbdassignment.feature.recurring.data.RecurringExpenseEntity
import androidx.room.withTransaction

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        RecurringExpenseEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    abstract fun categoryDao(): CategoryDao

    abstract fun recurringExpenseDao(): RecurringExpenseDao

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
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS recurring_expenses (
                id TEXT NOT NULL,
                title TEXT NOT NULL,
                amount REAL NOT NULL,
                currencyCode TEXT NOT NULL,
                categoryId TEXT NOT NULL,
                categoryName TEXT NOT NULL,
                paymentMethod TEXT NOT NULL,
                frequency TEXT NOT NULL,
                startDateMillis INTEGER NOT NULL,
                endDateMillis INTEGER,
                isActive INTEGER NOT NULL,
                notes TEXT NOT NULL,
                lastGeneratedDateMillis INTEGER,
                PRIMARY KEY(id)
            )
            """.trimIndent()
                )
            }
        }
    }
}