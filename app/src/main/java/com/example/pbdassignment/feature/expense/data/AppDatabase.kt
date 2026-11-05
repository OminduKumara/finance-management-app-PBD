package com.example.pbdassignment.feature.expense.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pbdassignment.feature.category.data.CategoryDao
import com.example.pbdassignment.feature.category.data.CategoryEntity

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pbdassignment.feature.recurring.data.RecurringExpenseDao
import com.example.pbdassignment.feature.recurring.data.RecurringExpenseEntity
import com.example.pbdassignment.feature.income.data.IncomeDao
import com.example.pbdassignment.feature.income.data.IncomeEntity
import androidx.room.withTransaction

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        RecurringExpenseEntity::class,
        IncomeEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    abstract fun categoryDao(): CategoryDao

    abstract fun recurringExpenseDao(): RecurringExpenseDao

    abstract fun incomeDao(): IncomeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pbd_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                    .also { INSTANCE = it }
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
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
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
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
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS incomes (
                id TEXT NOT NULL,
                streamType TEXT NOT NULL,
                title TEXT NOT NULL,
                clientOrSource TEXT NOT NULL,
                rawAmount REAL NOT NULL,
                currency TEXT NOT NULL,
                exchangeRate REAL NOT NULL,
                lkrAmount REAL NOT NULL,
                date INTEGER NOT NULL,
                invoiceStatus TEXT NOT NULL,
                dueDate INTEGER,
                isSweptToGoal INTEGER NOT NULL DEFAULT 0,
                goalSweptAmount REAL NOT NULL DEFAULT 0.0,
                PRIMARY KEY(id)
            )
            """.trimIndent()
                )
            }
        }
    }
}