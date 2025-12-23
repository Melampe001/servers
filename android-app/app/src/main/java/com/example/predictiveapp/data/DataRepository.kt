package com.example.predictiveapp.data

import com.example.predictiveapp.utils.EncryptionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Repository for managing data persistence
 * Handles encrypted storage and retrieval of analysis data
 */
class DataRepository {
    private val encryptionManager = EncryptionManager()
    
    /**
     * Save analysis results to encrypted storage
     */
    suspend fun saveResults(data: List<Double>, analysisResults: Map<String, Any>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val jsonObject = JSONObject().apply {
                    put("timestamp", System.currentTimeMillis())
                    put("data", JSONArray(data))
                    put("results", JSONObject(analysisResults))
                }
                
                val plaintext = jsonObject.toString()
                val encrypted = encryptionManager.encryptString(plaintext)
                
                // In a real app, save to file or database
                // For now, just return success
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    /**
     * Load encrypted data from storage
     */
    suspend fun loadResults(): String? {
        return withContext(Dispatchers.IO) {
            try {
                // In a real app, load from file or database
                // Return null for now
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    /**
     * Export data to JSON format (encrypted)
     */
    suspend fun exportToJson(data: List<Double>): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val jsonObject = JSONObject().apply {
                    put("exported_at", System.currentTimeMillis())
                    put("data", JSONArray(data))
                }
                
                encryptionManager.encryptString(jsonObject.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    /**
     * Import data from encrypted JSON
     */
    suspend fun importFromJson(encryptedData: ByteArray): List<Double>? {
        return withContext(Dispatchers.IO) {
            try {
                val plaintext = encryptionManager.decryptString(encryptedData)
                val jsonObject = JSONObject(plaintext)
                val dataArray = jsonObject.getJSONArray("data")
                
                List(dataArray.length()) { i ->
                    dataArray.getDouble(i)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
