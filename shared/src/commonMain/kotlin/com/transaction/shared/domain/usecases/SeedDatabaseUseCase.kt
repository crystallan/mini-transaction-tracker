package com.transaction.shared.domain.usecases

import com.transaction.shared.domain.repository.TransactionRepository
import com.transaction.shared.domain.seed.SeedDataSource
import com.transaction.shared.storage.TransactionBlobBackup
import kotlinx.coroutines.flow.first

class SeedDatabaseUseCase(
    private val repository: TransactionRepository,
    private val seedDataSource: SeedDataSource,
    private val blobBackup: TransactionBlobBackup
) {
    suspend operator fun invoke() {
        blobBackup.ensureRestored()
        val existing = repository.getAllTransactions().first()
        if (existing.isEmpty()) {
            seedDataSource.loadSeedTransactions().forEach { transaction ->
                repository.insertTransaction(transaction)
            }
        }
    }
}