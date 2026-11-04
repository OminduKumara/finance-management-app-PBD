package com.example.pbdassignment.feature.income.data

import com.example.pbdassignment.feature.income.domain.CryptoTradeRecord
import com.example.pbdassignment.feature.income.domain.CurrencyType
import com.example.pbdassignment.feature.income.domain.IncomeRecord
import com.example.pbdassignment.feature.income.domain.IncomeSource
import com.example.pbdassignment.feature.income.domain.MilestoneStatus

fun IncomeDto.toDomain(): IncomeRecord = IncomeRecord(
        id = id,
        userId = userId,
        title = title,
        source = IncomeSource.entries.firstOrNull { it.name == source } ?: IncomeSource.SALARY,
        currency = CurrencyType.entries.firstOrNull { it.name == currency } ?: CurrencyType.LKR,
        originalAmount = originalAmount,
        exchangeRate = exchangeRate,
        convertedAmountLkr = convertedAmountLkr,
        dateTimestamp = dateTimestamp,
        note = note,
        baseSalaryLkr = baseSalaryLkr,
        bonusLkr = bonusLkr,
        clientName = clientName,
        invoiceNumber = invoiceNumber,
        milestoneStatus = MilestoneStatus.entries.firstOrNull { it.name == milestoneStatus },
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

fun CryptoTradeDto.toDomain(): CryptoTradeRecord = CryptoTradeRecord(
        id = id,
        userId = userId,
        assetSymbol = assetSymbol,
        buyPriceUsd = buyPriceUsd,
        sellPriceUsd = sellPriceUsd,
        quantity = quantity,
        realisedPnLUsd = realisedPnLUsd,
        realisedPnLLkr = realisedPnLLkr,
        exchangeRateLkr = exchangeRateLkr,
        dateTimestamp = dateTimestamp,
        exchange = exchange
)

fun CryptoTradeRecord.toDto(): CryptoTradeDto = CryptoTradeDto(
        id = id,
        userId = userId,
        assetSymbol = assetSymbol,
        buyPriceUsd = buyPriceUsd,
        sellPriceUsd = sellPriceUsd,
        quantity = quantity,
        realisedPnLUsd = realisedPnLUsd,
        realisedPnLLkr = realisedPnLLkr,
        exchangeRateLkr = exchangeRateLkr,
        dateTimestamp = dateTimestamp,
        exchange = exchange
)