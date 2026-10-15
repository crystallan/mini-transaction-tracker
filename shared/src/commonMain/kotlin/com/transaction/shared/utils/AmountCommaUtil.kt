package com.transaction.shared.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation


class NumberCommaVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val dot = original.indexOf('.')
        val intPart = if (dot >= 0) original.substring(0, dot) else original
        val rest = if (dot >= 0) original.substring(dot) else ""

        val grouped = buildString {
            intPart.forEachIndexed { index, c ->
                if (index > 0 && (intPart.length - index) % 3 == 0) append(',')
                append(c)
            }
        }
        val formatted = grouped + rest
        val totalCommas = grouped.length - intPart.length

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val o = offset.coerceIn(0, original.length)
                // Past the integer part, every comma is before the cursor
                return if (o > intPart.length) o + totalCommas
                else o + commasBefore(intPart.length, o)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val t = offset.coerceIn(0, formatted.length)
                return t - formatted.take(t).count { it == ',' }
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    /** Commas inserted before cursor [position] in an integer part of [intLength] digits. */
    private fun commasBefore(intLength: Int, position: Int): Int =
        (1 until position).count { (intLength - it) % 3 == 0 }
}