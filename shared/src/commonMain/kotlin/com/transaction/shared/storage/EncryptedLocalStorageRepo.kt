package com.transaction.shared.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class EncryptedLocalStorageRepo(
    private val dataStore: DataStore<Preferences>,
    private val keystoreManager: KeystoreManager
) {

    private companion object {
        const val TRANSACTIONS_KEY_ALIAS = "transactions_blob_key"
    }

    private val json = Json { ignoreUnknownKeys = true }
    private val listSerializer = ListSerializer(TransactionBlobDto.serializer())

    val transactionFlow: Flow<List<TransactionBlobDto>> = dataStore.data.map { prefs ->
        val encoded = prefs[TransactionPrefsKeys.TRANSACTIONS_JSON]
            ?: return@map emptyList()
        try {
            val encrypted = Base64.Default.decode(encoded)
            val decrypted = keystoreManager.decrypt(TRANSACTIONS_KEY_ALIAS, encrypted)
            json.decodeFromString(listSerializer, decrypted.decodeToString())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("EncryptedLocalStorageRepo: failed to read transactions, emitting empty list: $e")
            emptyList()
        }
    }

    suspend fun saveTransactions(transactions: List<TransactionBlobDto>) {
        val plain = json.encodeToString(listSerializer, transactions).encodeToByteArray()
        val encrypted = keystoreManager.encrypt(TRANSACTIONS_KEY_ALIAS, plain)
        val encoded = Base64.Default.encode(encrypted)
        dataStore.edit { prefs ->
            prefs[TransactionPrefsKeys.TRANSACTIONS_JSON] = encoded
        }
    }
}