package com.transaction.shared.domain.usecases

import com.transaction.shared.domain.model.Transaction
import com.transaction.shared.domain.repository.TransactionRepository

class DeleteTransactionUseCase (
    private val repository: TransactionRepository
){
    suspend operator fun invoke(transaction: Transaction){
        repository.deleteTransaction(transaction)
    }
}