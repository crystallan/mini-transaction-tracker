package com.transaction.mini_transaction_tracker.core.domain.usecase

import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.model.ValidationResult
import com.transaction.mini_transaction_tracker.core.domain.repository.TransactionRepository


class AddTransactionUseCase (
    private val repository: TransactionRepository,
    private val validateTransaction: ValidateTransactionUseCase
){
    suspend operator fun invoke(transaction: Transaction) {
        val result = validateTransaction(transaction)
        if ( result is ValidationResult.Invalid){
            throw IllegalArgumentException(result.message)
        }
        repository.insertTransaction(transaction)
    }
}