package com.transaction.shared.storage

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.errSecDuplicateItem
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.posix.memcpy

private const val KEYCHAIN_SERVICE = "com.transaction.shared.keystore"

@OptIn(ExperimentalForeignApi::class)
class IosKeystoreManager : KeystoreManager {

    private val aesGcm = CryptographyProvider.Default.get(AES.GCM)

    override fun encrypt(alias: String, plaintext: ByteArray): ByteArray =
        cipherFor(getOrCreateKeyBytes(alias)).encryptBlocking(plaintext)

    override fun decrypt(alias: String, ciphertext: ByteArray): ByteArray {
        val keyBytes = readKey(alias)
            ?: error("No encryption key found for alias '$alias'")
        return cipherFor(keyBytes).decryptBlocking(ciphertext)
    }

    private fun cipherFor(keyBytes: ByteArray) =
        aesGcm.keyDecoder()
            .decodeFromByteArrayBlocking(AES.Key.Format.RAW, keyBytes)
            .cipher()

    private fun getOrCreateKeyBytes(alias: String): ByteArray {
        readKey(alias)?.let { return it }
        val key = aesGcm.keyGenerator(AES.Key.Size.B256).generateKeyBlocking()
        val bytes = key.encodeToByteArrayBlocking(AES.Key.Format.RAW)
        // If another caller stored a key first, use theirs so all data shares one key
        return if (writeKey(alias, bytes)) bytes
        else readKey(alias) ?: error("Keychain reported a duplicate key for '$alias' but it could not be read")
    }

    private fun baseQuery(alias: String) = CFDictionaryCreateMutable(
        null, 0, kCFTypeDictionaryKeyCallBacks.ptr, kCFTypeDictionaryValueCallBacks.ptr
    ).also { q ->
        CFDictionaryAddValue(q, kSecClass, kSecClassGenericPassword)
        val service = CFBridgingRetain(KEYCHAIN_SERVICE)
        val account = CFBridgingRetain(alias)
        CFDictionaryAddValue(q, kSecAttrService, service)
        CFDictionaryAddValue(q, kSecAttrAccount, account)
        CFRelease(service) // the dictionary retains its values
        CFRelease(account)
    }

    private fun readKey(alias: String): ByteArray? {
        val query = baseQuery(alias)
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)
        return try {
            memScoped {
                val result = alloc<CFTypeRefVar>()
                when (val status = SecItemCopyMatching(query, result.ptr)) {
                    errSecSuccess -> (CFBridgingRelease(result.value) as NSData).toByteArray()
                    errSecItemNotFound -> null
                    else -> error("Keychain read failed: OSStatus $status")
                }
            }
        } finally {
            CFRelease(query)
        }
    }

    /** Returns true if the key was stored, false if a key for this alias already exists. */
    private fun writeKey(alias: String, bytes: ByteArray): Boolean {
        val query = baseQuery(alias)
        val data = CFBridgingRetain(bytes.toNSData())
        CFDictionaryAddValue(query, kSecValueData, data)
        CFDictionaryAddValue(query, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
        try {
            return when (val status = SecItemAdd(query, null)) {
                errSecSuccess -> true
                errSecDuplicateItem -> false
                else -> error("Keychain write failed: OSStatus $status")
            }
        } finally {
            CFRelease(data)
            CFRelease(query)
        }
    }

    @OptIn(BetaInteropApi::class)
    private fun ByteArray.toNSData(): NSData = usePinned {
        NSData.create(bytes = it.addressOf(0), length = size.toULong())
    }

    private fun NSData.toByteArray(): ByteArray {
        val out = ByteArray(length.toInt())
        if (out.isNotEmpty()) out.usePinned { memcpy(it.addressOf(0), bytes, length) }
        return out
    }
}