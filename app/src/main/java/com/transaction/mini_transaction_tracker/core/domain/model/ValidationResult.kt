package com.transaction.mini_transaction_tracker.core.domain.model

sealed class ValidationResult {
    object  Valid : ValidationResult()
    data class Invalid(val field: ValidationField, val message: String) : ValidationResult()

}
