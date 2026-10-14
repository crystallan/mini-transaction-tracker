package com.transaction.shared.utils

import io.github.kormium.decimal.Decimal
import io.github.kormium.decimal.RoundingMode

enum class Currency(val code: String) {
    KES("KES"),
    USD("USD")
}

object CurrencyUtils {

    private val KES_TO_USD_RATE = Decimal("0.0129")

    fun format(amount: Decimal, currency: Currency = Currency.KES): String {
        val rounded = amount.setScale(2, RoundingMode.HALF_UP)
        return "${currency.code} ${groupThousands(rounded.toPlainString())}"
    }

    fun convertAndFormat(amount: Decimal, from: Currency, to: Currency): String {
        if (from == to) return format(amount, to)

        val converted = when (from) {
            Currency.KES if to == Currency.USD ->
                amount * KES_TO_USD_RATE

            Currency.USD if to == Currency.KES ->
                amount.div(KES_TO_USD_RATE, scale = 2, roundingMode = RoundingMode.HALF_UP)

            else -> throw IllegalArgumentException("No rate available for $from -> $to")
        }.setScale(2, RoundingMode.HALF_UP)

        return format(converted, to)
    }
    private fun groupThousands(plain: String): String {
        val negative = plain.startsWith("-")
        val unsigned = if (negative) plain.substring(1) else plain
        val parts = unsigned.split(".")
        val intPart = parts[0]
        val fracPart = parts.getOrElse(1) { "00" }

        val grouped = intPart.reversed()
            .mapIndexed { index, c -> if (index != 0 && index % 3 == 0) ",$c" else "$c" }
            .joinToString("")
            .reversed()

        return buildString {
            if (negative) append('-')
            append(grouped)
            append('.')
            append(fracPart)
        }
    }
}