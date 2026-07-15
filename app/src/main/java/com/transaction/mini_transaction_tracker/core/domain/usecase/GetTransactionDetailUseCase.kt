package com.transaction.mini_transaction_tracker.core.domain.usecase

import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.repository.TransactionRepository

class GetTransactionDetailUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke (id : Int) : Transaction?{
        return repository.getTransactionById(id)
    }
}