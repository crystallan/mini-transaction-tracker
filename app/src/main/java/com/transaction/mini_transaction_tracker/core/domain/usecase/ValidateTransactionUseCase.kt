package com.transaction.mini_transaction_tracker.core.domain.usecase

import com.transaction.mini_transaction_tracker.core.domain.model.FieldError
import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.model.ValidationField
import com.transaction.mini_transaction_tracker.core.domain.model.ValidationResult
import java.math.BigDecimal
import java.time.LocalDateTime

class ValidateTransactionUseCase {

    operator fun invoke(transaction: Transaction): ValidationResult =
        build(validate(transaction.amount, transaction.description, transaction.date))

    operator fun invoke(
        rawAmount: String,
        description: String,
        date: LocalDateTime
    ): ValidationResult =
        build(validate(rawAmount.toBigDecimalOrNull(), description, date))

    private fun validate(
        amount: BigDecimal?,
        description: String,
        date: LocalDateTime
    ): List<FieldError> {
        val errors = mutableListOf<FieldError>()

        when {
            amount == null -> errors += FieldError(ValidationField.AMOUNT, "Enter a valid number.")
            amount <= BigDecimal.ZERO -> errors += FieldError(
                ValidationField.AMOUNT,
                "Amount must be greater than zero."
            )
        }
        if (description.isBlank()) {
            errors += FieldError(ValidationField.DESCRIPTION, "Description cannot be empty.")
        }
        if (date.isAfter(LocalDateTime.now())) {
            errors += FieldError(ValidationField.DATE, "Date cannot be in the future.")
        }

        return errors
    }

    private fun build(errors: List<FieldError>) =
        if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
}