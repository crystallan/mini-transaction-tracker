package com.transaction.shared.domain.usecases

import com.transaction.shared.domain.repository.TransactionRepository
import com.transaction.shared.domain.seed.SeedDataSource
import com.transaction.shared.storage.SeedStateStore
import com.transaction.shared.storage.TransactionBlobBackup
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SeedDatabaseUseCase(
    private val repository: TransactionRepository,
    private val seedDataSource: SeedDataSource,
    private val blobBackup: TransactionBlobBackup,
    private val seedState: SeedStateStore
) {
    // Must be a Koin `single` so every caller shares this lock
    private val mutex = Mutex()

    suspend operator fun invoke() {
        mutex.withLock {
            // Restore the user's own data first, so seed data never lands on top of a recoverable backup
            blobBackup.ensureRestored()

            if (seedState.isSeeded()) return@withLock

            // Installs from before this flag existed (or with restored data) already have rows:
            // just mark them as seeded
            if (!repository.hasTransactions()) {
                repository.insertTransactions(seedDataSource.loadSeedTransactions())
            }
            // If the app dies before this line, the next launch sees rows and only sets the flag
            seedState.markSeeded()
        }
    }
}