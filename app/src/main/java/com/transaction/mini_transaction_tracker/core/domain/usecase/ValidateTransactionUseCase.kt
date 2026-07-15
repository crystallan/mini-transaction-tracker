package com.transaction.mini_transaction_tracker.core.domain.usecase

import com.transaction.mini_transaction_tracker.core.domain.model.FieldError
import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.model.ValidationField
import com.transaction.mini_transaction_tracker.core.domain.model.ValidationResult
import java.math.BigDecimal
import java.time.LocalDateTime

class ValidateTransactionUseCase {

    operator fun invoke( transaction: Transaction) : ValidationResult {
        val errors = mutableListOf<FieldError>()

        if (transaction.amount <= BigDecimal.ZERO) {
            errors += FieldError(ValidationField.AMOUNT, "Amount must be greater than zero.")
        }
        if (transaction.description.isBlank()) {
            errors += FieldError(ValidationField.DESCRIPTION, "Description cannot be empty.")
        }
        if (transaction.date.isAfter(LocalDateTime.now())) {
            errors += FieldError(ValidationField.DATE, "Date cannot be in the future.")
        }

        return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}