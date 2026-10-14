package com.transaction.shared.storage

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val ANDROID_KEYSTORE = "AndroidKeyStore"
private const val GCM_IV_LENGTH = 12
private const val GCM_TAG_LENGTH_BITS = 128

class AndroidKeystoreManager : KeystoreManager {

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private fun getOrCreateSecretKey(alias: String): SecretKey {
        (keyStore.getKey(alias, null) as? SecretKey)?.let { return it }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    override fun encrypt(alias: String, plaintext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey(alias))
        val iv = cipher.iv
        return iv + cipher.doFinal(plaintext)
    }

    override fun decrypt(alias: String, ciphertext: ByteArray): ByteArray {
        val iv = ciphertext.copyOfRange(0, GCM_IV_LENGTH)
        val actualCiphertext = ciphertext.copyOfRange(GCM_IV_LENGTH, ciphertext.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(alias),
            GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        )
        return cipher.doFinal(actualCiphertext)
    }
}