package com.transaction.shared.storage

import com.transaction.shared.domain.repository.TransactionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class TransactionBlobBackup(
    private val repository: TransactionRepository,
    private val blobRepo: EncryptedLocalStorageRepo,
    private val scope: CoroutineScope
) {
    private val restoreMutex = Mutex()
    private var restoreDone = false
    private var restoreFailed = false
    private var job: Job? = null

    suspend fun ensureRestored() = restoreMutex.withLock {
        if (restoreDone) return@withLock
        try {
            if (!repository.hasTransactions()) {
                // Unreadable or absent blobs come back as an empty list.
                val snapshot = blobRepo.transactionFlow.first()
                // One transaction: a partial restore must never be snapshotted over the full blob
                repository.insertTransactions(snapshot.map { it.toTransaction() })
                snapshot.forEach { repository.insertTransaction(it.toTransaction()) }
                println("TransactionBlobBackup: restored ${snapshot.size} transactions from snapshot")
            } else {
                println("TransactionBlobBackup: restore skipped, Room already has data")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            restoreFailed = true
            println("TransactionBlobBackup: restore failed, snapshots paused: $e")
        }
        restoreDone = true
    }

    fun start() {
        if (job != null) return
        job = scope.launch {
            ensureRestored()
            if (restoreFailed) return@launch // don't overwrite a blob we couldn't merge
            repository.getAllTransactions()
                .map { list -> list.map { it.toBlobDto() } }
                .distinctUntilChanged()
                .debounce(500.milliseconds)
                .collect { snapshot ->
                    try {
                        blobRepo.saveTransactions(snapshot)
                        println("TransactionBlobBackup: snapshot saved (${snapshot.size} transactions)")                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        println("TransactionBlobBackup: snapshot write failed: $e")
                    }
                }
        }
    }
}