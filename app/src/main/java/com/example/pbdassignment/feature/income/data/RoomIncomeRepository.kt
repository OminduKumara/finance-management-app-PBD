package com.example.pbdassignment.feature.income.data

import com.example.pbdassignment.core.model.IncomeItem
import com.example.pbdassignment.core.model.InvoiceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomIncomeRepository(
    private val incomeDao: IncomeDao
) : LocalIncomeRepository {

    override fun observeIncomes(): Flow<List<IncomeItem>> {
        return incomeDao.observeIncomes().map { list ->
            list.map { it.toIncomeItem() }
        }
    }

    override suspend fun addIncome(income: IncomeItem) {
        incomeDao.insertIncome(income.toIncomeEntity())
    }

    override suspend fun updateIncome(income: IncomeItem) {
        incomeDao.updateIncome(income.toIncomeEntity())
    }

    override suspend fun deleteIncome(id: String) {
        incomeDao.deleteIncome(id)
    }

    override suspend fun clearIncomes() {
        incomeDao.clearIncomes()
    }
}

fun IncomeEntity.toIncomeItem(): IncomeItem {
    return IncomeItem(
        id = id,
        streamType = streamType,
        title = title,
        clientOrSource = clientOrSource,
        rawAmount = rawAmount,
        currency = currency,
        exchangeRate = exchangeRate,
        lkrAmount = lkrAmount,
        date = date,
        invoiceStatus = try {
            InvoiceStatus.valueOf(invoiceStatus)
        } catch (e: Exception) {
            InvoiceStatus.RECEIVED
        },
        dueDate = dueDate,
        isSweptToGoal = isSweptToGoal,
        goalSweptAmount = goalSweptAmount
    )
}

fun IncomeItem.toIncomeEntity(): IncomeEntity {
    return IncomeEntity(
        id = id,
        streamType = streamType,
        title = title,
        clientOrSource = clientOrSource,
        rawAmount = rawAmount,
        currency = currency,
        exchangeRate = exchangeRate,
        lkrAmount = lkrAmount,
        date = date,
        invoiceStatus = invoiceStatus.name,
        dueDate = dueDate,
        isSweptToGoal = isSweptToGoal,
        goalSweptAmount = goalSweptAmount
    )
}

