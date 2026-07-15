package com.transaction.mini_transaction_tracker.core.domain.usecase

import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.repository.TransactionRepository

class DeleteTransactionUseCase (
    private val repository: TransactionRepository
){
    suspend operator fun invoke(transaction: Transaction){
        repository.deleteTransaction(transaction)
    }
}