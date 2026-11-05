package com.example.pbdassignment.feature.income.domain

import java.math.BigDecimal
import java.math.RoundingMode

class ConvertToLkr {

    data class ConversionResult(
        val originalAmount: Double,
        val exchangeRate: Double,
        val convertedAmountLkr: Double,
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    operator fun invoke(
        originalAmount: Double,
        exchangeRate: Double

    ): ConversionResult{
        if (originalAmount < 0){
            return ConversionResult(
                originalAmount = originalAmount,
                exchangeRate = exchangeRate,
                convertedAmountLkr = 0.0,
                isValid = false,
                errorMessage = "Amount cannot be negative"
            )
        }

        if(exchangeRate <= 0){
            return ConversionResult(
                originalAmount = originalAmount,
                exchangeRate = exchangeRate,
                convertedAmountLkr = 0.0,
                isValid = false,
                errorMessage = "Exchange rate must be greater than zero"

            )
        }

        val converted = BigDecimal(originalAmount)
            .multiply(BigDecimal(exchangeRate))
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

        return ConversionResult(
            originalAmount = originalAmount,
            exchangeRate = exchangeRate,
            convertedAmountLkr = converted,
            isValid = true
        )


    }

}