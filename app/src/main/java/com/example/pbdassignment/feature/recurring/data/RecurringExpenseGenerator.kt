package com.example.pbdassignment.feature.recurring.data

import androidx.room.withTransaction
import com.example.pbdassignment.feature.expense.data.AppDatabase
import com.example.pbdassignment.feature.expense.data.ExpenseEntity
import java.util.Calendar
import java.util.UUID

class RecurringExpenseGenerator(
    private val database: AppDatabase
) {

    suspend fun generateDueExpenses() {
        database.withTransaction {
            val recurringDao = database.recurringExpenseDao()
            val expenseDao = database.expenseDao()

            val activeExpenses = recurringDao.getActiveRecurringExpenses()
            val now = System.currentTimeMillis()

            for (recurring in activeExpenses) {

                var occurrenceDate = recurring.lastGeneratedDateMillis ?: 0L

                var nextDate = if (recurring.lastGeneratedDateMillis == null) {
                    recurring.startDateMillis
                } else {
                    getNextDate(occurrenceDate, recurring.frequency)
                }

                while (nextDate <= now) {

                    if (
                        recurring.endDateMillis != null &&
                        nextDate > recurring.endDateMillis
                    ) break

                    val expenseId = UUID.nameUUIDFromBytes(
                        "${recurring.id}_$nextDate".toByteArray()
                    ).toString()

                    expenseDao.insertExpense(
                        ExpenseEntity(
                            id = expenseId,
                            title = recurring.title,
                            amount = recurring.amount,
                            currencyCode = recurring.currencyCode,
                            categoryId = recurring.categoryId,
                            categoryName = recurring.categoryName,
                            dateMillis = nextDate,
                            isCommitted = true,
                            paymentMethod = recurring.paymentMethod,
                            notes = recurring.notes
                        )
                    )

                    com.example.pbdassignment.core.firebase.FirebaseManager.syncExpense(
                        com.example.pbdassignment.feature.expense.domain.Expense(
                            id = expenseId,
                            title = recurring.title,
                            amount = recurring.amount,
                            currencyCode = recurring.currencyCode,
                            categoryId = recurring.categoryId,
                            categoryName = recurring.categoryName,
                            dateMillis = nextDate,
                            isCommitted = true,
                            paymentMethod = recurring.paymentMethod,
                            notes = recurring.notes
                        )
                    )

                    com.example.pbdassignment.core.data.FinanceRepository.addExpense(
                        com.example.pbdassignment.core.model.ExpenseItem(
                            id = expenseId,
                            amount = recurring.amount,
                            category = recurring.categoryName,
                            nature = com.example.pbdassignment.core.model.ExpenseNature.COMMITTED,
                            paymentSource = when (recurring.paymentMethod.lowercase()) {
                                "credit card", "credit_card", "card" -> com.example.pbdassignment.core.model.PaymentSource.PRIMARY_CARD
                                "bank transfer", "debit card" -> com.example.pbdassignment.core.model.PaymentSource.SECONDARY_ACCOUNT
                                else -> com.example.pbdassignment.core.model.PaymentSource.CASH
                            },
                            merchantPreset = recurring.title,
                            notes = recurring.notes
                        )
                    )

                    occurrenceDate = nextDate
                    nextDate = getNextDate(
                        occurrenceDate,
                        recurring.frequency
                    )
                }

                recurringDao.updateRecurringExpense(
                    recurring.copy(
                        lastGeneratedDateMillis = occurrenceDate
                    )
                )
            }
        }
    }

    private fun getNextDate(
        previousDate: Long,
        frequency: String
    ): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = previousDate
        }

        when (frequency) {
            "DAILY" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            "WEEKLY" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> calendar.add(Calendar.MONTH, 1)
            "YEARLY" -> calendar.add(Calendar.YEAR, 1)
            else -> return Long.MAX_VALUE
        }

        return calendar.timeInMillis
    }
}