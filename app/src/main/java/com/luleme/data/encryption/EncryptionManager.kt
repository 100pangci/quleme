package com.quleme.data.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor() {
    private val keyAlias = "quleme_master_key"
    private val androidKeyStore = "AndroidKeyStore"
    private val transformation = "AES/GCM/NoPadding"

    init {
        createKeyIfNotExists()
    }

    private fun createKeyIfNotExists() {
        val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }
        if (!keyStore.containsAlias(keyAlias)) {
            generateKey()
        }
    }

    fun encryptData(plainText: String): String {
        try {
            val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }
            val key = keyStore.getKey(keyAlias, null) as SecretKey
            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val iv = cipher.iv
            val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            // Return Base64(IV + Encrypted)
            // IV is typically 12 bytes for GCM
            val combined = iv + encrypted
            return Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            throw EncryptionException("Failed to encrypt data", e)
        }
    }

    fun decryptData(encryptedText: String): String {
        try {
            val decoded = Base64.decode(encryptedText, Base64.DEFAULT)
            if (decoded.size <= GCM_IV_LENGTH_BYTES) {
                throw IllegalArgumentException("Encrypted payload is too short")
            }
            
            // Extract IV (first 12 bytes)
            val iv = decoded.copyOfRange(0, GCM_IV_LENGTH_BYTES)
            val encrypted = decoded.copyOfRange(GCM_IV_LENGTH_BYTES, decoded.size)

            val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }
            val key = keyStore.getKey(keyAlias, null) as SecretKey
            val cipher = Cipher.getInstance(transformation)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val decrypted = cipher.doFinal(encrypted)
            return String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            throw DecryptionException("Failed to decrypt data", e)
        }
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            androidKeyStore
        )

        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false) 
                .build()
        )

        keyGenerator.generateKey()
    }

    class EncryptionException(message: String, cause: Throwable) : IllegalStateException(message, cause)
    class DecryptionException(message: String, cause: Throwable) : IllegalStateException(message, cause)

    private companion object {
        const val GCM_IV_LENGTH_BYTES = 12
    }
}
