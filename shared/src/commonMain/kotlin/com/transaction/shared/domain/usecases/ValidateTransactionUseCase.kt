package com.transaction.shared.domain.usecases

import com.transaction.shared.domain.model.FieldError
import com.transaction.shared.domain.model.Transaction
import com.transaction.shared.domain.model.ValidationField
import com.transaction.shared.domain.model.ValidationResult
import com.transaction.shared.domain.utils.toDecimalOrNull
import io.github.kormium.decimal.Decimal
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class ValidateTransactionUseCase {

    operator fun invoke(transaction: Transaction): ValidationResult =
        build(validate(transaction.amount, transaction.description, transaction.date))

    operator fun invoke(
        rawAmount: String,
        description: String,
        date: LocalDateTime
    ): ValidationResult =
        build(validate(rawAmount.toDecimalOrNull(), description, date))

    private fun validate(
        amount: Decimal?,
        description: String,
        date: LocalDateTime
    ): List<FieldError> {
        val errors = mutableListOf<FieldError>()

        when {
            amount == null -> errors += FieldError(ValidationField.AMOUNT, "Enter a valid number.")
            amount <= Decimal.ZERO -> errors += FieldError(
                ValidationField.AMOUNT,
                "Amount must be greater than zero."
            )
        }
        if (description.isBlank()) {
            errors += FieldError(ValidationField.DESCRIPTION, "Description cannot be empty.")
        }
        if (date>currentDateTime()) {
            errors += FieldError(ValidationField.DATE, "Date cannot be in the future.")
        }

        return errors
    }

    private fun build(errors: List<FieldError>) =
        if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)

    private fun currentDateTime(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}