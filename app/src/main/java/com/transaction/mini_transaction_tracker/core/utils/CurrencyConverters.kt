package com.transaction.mini_transaction_tracker.core.utils

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

enum class Currency(val code: String, val locale: Locale) {
    KES("KES", Locale("en", "KE")),
    USD("USD", Locale.US)
}

object CurrencyUtils {

    private val KES_TO_USD_RATE = BigDecimal("0.01")

    private fun formatterFor(currency: Currency): NumberFormat =
        NumberFormat.getCurrencyInstance(currency.locale).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }

    fun format(amount: BigDecimal, currency: Currency = Currency.KES): String =
        formatterFor(currency).format(amount)

    fun convertAndFormat(amount: BigDecimal, from: Currency, to: Currency): String {
        if (from == to) return format(amount, to)

        val converted = when {
            from == Currency.KES && to == Currency.USD ->
                amount.multiply(KES_TO_USD_RATE, MathContext.DECIMAL64)
            from == Currency.USD && to == Currency.KES ->
                amount.divide(KES_TO_USD_RATE, MathContext.DECIMAL64)
            else -> throw IllegalArgumentException("No rate available for $from -> $to")
        }.setScale(2, RoundingMode.HALF_UP)

        return format(converted, to)
    }
}