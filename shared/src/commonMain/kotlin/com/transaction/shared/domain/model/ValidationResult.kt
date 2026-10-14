package com.transaction.shared.domain.model

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<FieldError>) : ValidationResult()
}
data class FieldError(val field: ValidationField, val message: String)