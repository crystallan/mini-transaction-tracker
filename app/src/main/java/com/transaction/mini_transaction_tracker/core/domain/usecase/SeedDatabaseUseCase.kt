package com.transaction.mini_transaction_tracker.core.domain.usecase

import com.transaction.mini_transaction_tracker.core.domain.repository.TransactionRepository
import com.transaction.mini_transaction_tracker.core.seed.SeedDataSource
import kotlinx.coroutines.flow.first

class SeedDatabaseUseCase(
    private val repository : TransactionRepository,
    private val seedDataSource : SeedDataSource
) {
    suspend operator fun invoke(){
        val existing = repository.getAllTransactions().first()
        if (existing.isEmpty()){
            seedDataSource.loadSeedTransactions().forEach { transaction ->
                repository.insertTransaction(transaction)
            }
        }
    }
}
