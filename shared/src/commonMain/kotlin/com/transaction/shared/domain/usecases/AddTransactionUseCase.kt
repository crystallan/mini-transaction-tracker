package com.transaction.shared.domain.usecases

import com.transaction.shared.domain.model.Transaction
import com.transaction.shared.domain.model.ValidationResult
import com.transaction.shared.domain.repository.TransactionRepository

class AddTransactionUseCase (
    private val repository: TransactionRepository,
    private val validateTransaction: ValidateTransactionUseCase
){
    suspend operator fun invoke(transaction: Transaction) {
        val result = validateTransaction(transaction)
        if ( result is ValidationResult.Invalid){
            val combinedMessage = result.errors.joinToString(separator = " ") { it.message }
            throw IllegalArgumentException(combinedMessage)
        }
        repository.insertTransaction(transaction)
    }
}