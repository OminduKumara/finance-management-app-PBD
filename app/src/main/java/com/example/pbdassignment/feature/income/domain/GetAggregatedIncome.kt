package com.example.pbdassignment.feature.income.domain


import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar

class GetAggregatedIncome {

    data class AggregatedIncome(
        val totalReceivedLkr: Double,
        val salarySumLkr: Double,
        val freelanceSumLkr: Double,
        val adsenseSumLkr: Double,
        val cryptoPnLSumLkr: Double,
        val pendingFreelanceLkr: Double,         // Kavindu often forgets to invoice!
        val pendingFreelanceCount: Int,
        val incomeList: List<IncomeRecord>
    )

    operator fun invoke(
        incomes: List<IncomeRecord>,
        cryptoTrades: List<CryptoTradeRecord>,
        month: Int,      // Calendar.MONTH (0-indexed)
        year: Int
    ): AggregatedIncome {
        val filtered = incomes.filter { record ->
            val cal = Calendar.getInstance().apply { timeInMillis = record.dateTimestamp }
            cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year
        }

        val filteredCrypto = cryptoTrades.filter { trade ->
            val cal = Calendar.getInstance().apply { timeInMillis = trade.dateTimestamp }
            cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year
        }

        val salary = filtered
            .filter { it.source == IncomeSource.SALARY }
            .sumOf { it.convertedAmountLkr }

        val freelanceReceived = filtered
            .filter { it.source == IncomeSource.FREELANCE && it.milestoneStatus == MilestoneStatus.RECEIVED }
            .sumOf { it.convertedAmountLkr }

        val pendingFreelance = filtered
            .filter {
                it.source == IncomeSource.FREELANCE &&
                        (it.milestoneStatus == MilestoneStatus.PENDING || it.milestoneStatus == MilestoneStatus.INVOICED)
            }

        val adsense = filtered
            .filter { it.source == IncomeSource.ADSENSE }
            .sumOf { it.convertedAmountLkr }

        val cryptoPnL = filteredCrypto.sumOf { it.realisedPnLLkr }

        // Only count positive crypto P&L as income; losses offset
        val total = listOf(salary, freelanceReceived, adsense, cryptoPnL.coerceAtLeast(0.0))
            .sumOf { it }

        return AggregatedIncome(
            totalReceivedLkr = round(total),
            salarySumLkr = round(salary),
            freelanceSumLkr = round(freelanceReceived),
            adsenseSumLkr = round(adsense),
            cryptoPnLSumLkr = round(cryptoPnL),
            pendingFreelanceLkr = round(pendingFreelance.sumOf { it.convertedAmountLkr }),
            pendingFreelanceCount = pendingFreelance.size,
            incomeList = filtered
        )
    }

    private fun round(value: Double): Double =
        BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
}