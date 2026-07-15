package com.transaction.mini_transaction_tracker.core.domain.model

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<FieldError>) : ValidationResult()
}
data class FieldError(val field: ValidationField, val message: String)
