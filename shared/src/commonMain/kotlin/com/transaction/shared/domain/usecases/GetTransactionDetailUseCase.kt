package com.transaction.shared.domain.usecases

import com.transaction.shared.domain.model.Transaction
import com.transaction.shared.domain.repository.TransactionRepository

class GetTransactionDetailUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke (id : Int) : Transaction?{
        return repository.getTransactionById(id)
    }
}