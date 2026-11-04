package com.example.pbdassignment.feature.income.data

import com.example.pbdassignment.feature.income.domain.CurrencyType
import com.example.pbdassignment.feature.income.domain.IncomeRecord
import com.example.pbdassignment.feature.income.domain.IncomeSource
import com.example.pbdassignment.feature.income.domain.MilestoneStatus

fun IncomeDto.toDomain(): IncomeRecord = IncomeRecord(
        id = id,
        userId = userId,
        title = title,
        source = IncomeSource.valueOf(source),
        currency = CurrencyType.valueOf(currency),
        originalAmount = originalAmount,
        exchangeRate = exchangeRate,
        convertedAmountLkr = convertedAmountLkr,
        dateTimestamp = dateTimestamp,
        note = note,
        baseSalaryLkr = baseSalaryLkr,
        bonusLkr = bonusLkr,
        clientName = clientName,
        invoiceNumber = invoiceNumber,
        milestoneStatus = milestoneStatus?.let { MilestoneStatus.valueOf(it) },
        linkedCryptoTradeId = linkedCryptoTradeId
)

fun IncomeRecord.toDto(): IncomeDto = IncomeDto(
        id = id,
        userId = userId,
        title = title,
        source = source.name,
        currency = currency.name,
        originalAmount = originalAmount,
        exchangeRate = exchangeRate,
        convertedAmountLkr = convertedAmountLkr,
        dateTimestamp = dateTimestamp,
        note = note,
        baseSalaryLkr = baseSalaryLkr,
        bonusLkr = bonusLkr,
        clientName = clientName,
        invoiceNumber = invoiceNumber,
        milestoneStatus = milestoneStatus?.name,
        linkedCryptoTradeId = linkedCryptoTradeId
)