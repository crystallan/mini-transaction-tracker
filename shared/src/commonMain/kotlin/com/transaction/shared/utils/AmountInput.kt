package com.transaction.shared.utils

/** Input rules for money amounts typed by the user. Pure functions, so they're easy to unit test. */
object AmountInput {
    const val MAX_INTEGER_DIGITS = 12
    const val MAX_DECIMAL_DIGITS = 2

    /**
     * Returns the normalized amount text, or null to reject the edit (the field keeps its previous value).
     * Allows intermediate states like "12." so the user can keep typing.
     */
    fun sanitize(raw: String): String? {
        if (raw.isEmpty()) return ""
        // Only ASCII digits and one '.': blocks letters, '-', spaces and ',' from hardware keyboards and paste.
        // ',' is rejected, not stripped: in comma-decimal locales "12,5" means 12.5, and stripping it would
        // silently turn it into 125.
        if (raw.any { it != '.' && it !in '0'..'9' }) return null
        if (raw.count { it == '.' } > 1) return null

        val dot = raw.indexOf('.')
        val intPart = if (dot >= 0) raw.substring(0, dot) else raw
        val decimalPart = if (dot >= 0) raw.substring(dot + 1) else null

        if (decimalPart != null && decimalPart.length > MAX_DECIMAL_DIGITS) return null

        // "007" -> "7", "000" -> "0", ".5" -> "0.5"
        val normalizedInt = intPart.trimStart('0').ifEmpty { "0" }
        if (normalizedInt.length > MAX_INTEGER_DIGITS) return null

        return if (decimalPart != null) "$normalizedInt.$decimalPart" else normalizedInt
    }

    /** Completes an intermediate value for submission: "12." -> "12". */
    fun finalize(text: String): String = text.removeSuffix(".")
}