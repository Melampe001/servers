package com.example.predictiveapp.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Encryption manager using AES-256-GCM with Android Keystore
 * Provides secure encryption/decryption for local data storage
 */
class EncryptionManager {
    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "predictive_app_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val IV_SIZE = 12
    }
    
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }
    
    /**
     * Initialize or retrieve the encryption key from Android Keystore
     */
    private fun getOrCreateKey(): SecretKey {
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            createKey()
        }
    }
    
    /**
     * Create a new AES-256 key in Android Keystore
     */
    private fun createKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        
        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }
    
    /**
     * Encrypt data using AES-256-GCM
     * @param plaintext Data to encrypt
     * @return Encrypted data (IV + ciphertext)
     */
    fun encrypt(plaintext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext)
        
        // Combine IV and ciphertext
        return iv + ciphertext
    }
    
    /**
     * Decrypt data using AES-256-GCM
     * @param encryptedData Encrypted data (IV + ciphertext)
     * @return Decrypted plaintext
     */
    fun decrypt(encryptedData: ByteArray): ByteArray {
        val iv = encryptedData.take(IV_SIZE).toByteArray()
        val ciphertext = encryptedData.drop(IV_SIZE).toByteArray()
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
        
        return cipher.doFinal(ciphertext)
    }
    
    /**
     * Encrypt string data
     */
    fun encryptString(plaintext: String): ByteArray {
        return encrypt(plaintext.toByteArray(Charsets.UTF_8))
    }
    
    /**
     * Decrypt to string
     */
    fun decryptString(encryptedData: ByteArray): String {
        return decrypt(encryptedData).toString(Charsets.UTF_8)
    }
}
