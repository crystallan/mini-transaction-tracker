package com.transaction.shared.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation


class NumberCommaVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        if (original.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val parts = original.split(".")
        val intPart = parts[0]
        val decimalPart = if (parts.size > 1) ".${parts[1]}" else ""

        val formattedInt = if (intPart.isNotEmpty())
            groupThousands(intPart.toLong()) else intPart
        val formatted = formattedInt + decimalPart

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, original.length)
                val commasBeforeInFormatted = formattedInt
                    .take((safeOffset + (formattedInt.length - intPart.length)).coerceAtMost(formattedInt.length))
                    .count { it == ',' }
                return (safeOffset + commasBeforeInFormatted).coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, formatted.length)
                val commasBefore = formatted.take(safeOffset).count { it == ',' }
                return (safeOffset - commasBefore).coerceIn(0, original.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
    private fun groupThousands(value: Long): String {
        val negative = value < 0
        val digits = kotlin.math.abs(value).toString()
        val grouped = digits.reversed()
            .mapIndexed { index, c -> if (index != 0 && index % 3 == 0) ",$c" else "$c" }
            .joinToString("")
            .reversed()
        return if (negative) "-$grouped" else grouped
    }
}