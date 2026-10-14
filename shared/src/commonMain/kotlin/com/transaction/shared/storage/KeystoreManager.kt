package com.transaction.shared.storage

interface KeystoreManager {
    fun encrypt(alias: String, plaintext: ByteArray): ByteArray
    fun decrypt(alias: String, ciphertext: ByteArray): ByteArray
}