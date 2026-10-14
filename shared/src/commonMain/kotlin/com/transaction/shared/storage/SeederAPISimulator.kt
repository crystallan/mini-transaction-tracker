package com.transaction.shared.storage

import com.transaction.shared.domain.model.Transaction
import com.transaction.shared.domain.seed.SeedDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class SeederAPISimulator(private val seedDataSource: SeedDataSource) {

    suspend fun fetchTransactions(): List<Transaction> {
        delay(1000.milliseconds) // simulate network latency
        return withContext(Dispatchers.IO) {
            seedDataSource.loadSeedTransactions() // blocking asset read, so off the calling dispatcher
        }
    }
}