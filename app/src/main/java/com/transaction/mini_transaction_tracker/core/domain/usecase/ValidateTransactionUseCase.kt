package com.transaction.mini_transaction_tracker.core.domain.usecase

import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.model.ValidationField
import com.transaction.mini_transaction_tracker.core.domain.model.ValidationResult
import java.math.BigDecimal
import java.time.LocalDateTime

class ValidateTransactionUseCase {

    operator fun invoke( transaction: Transaction) : ValidationResult {
        if (transaction.amount <= BigDecimal.ZERO) {
            return ValidationResult.Invalid(
                field = ValidationField.AMOUNT,
                message = "The amount cannot be negative or zero."
            )
        }
        if (transaction.description.isBlank()) {
            return ValidationResult.Invalid(
                field = ValidationField.DESCRIPTION,
                message = "The description cannot be empty."
            )
        }
        if(transaction.date.isAfter(LocalDateTime.now())){
            return ValidationResult.Invalid(
                field = ValidationField.DATE,
                message = "The date cannot be in the future."
            )
        }
        return ValidationResult.Valid
    }
}
