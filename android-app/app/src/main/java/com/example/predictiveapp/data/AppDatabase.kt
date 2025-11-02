package com.example.predictiveapp.data

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase

/**
 * Room database for local data storage
 */
@Database(entities = [AnalysisEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun analysisDao(): AnalysisDao
}

/**
 * Entity representing a saved analysis
 */
@Entity(tableName = "analyses")
data class AnalysisEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val encryptedData: ByteArray,
    val mean: Double,
    val median: Double,
    val stdDev: Double,
    val trend: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnalysisEntry

        if (id != other.id) return false
        if (timestamp != other.timestamp) return false
        if (!encryptedData.contentEquals(other.encryptedData)) return false
        if (mean != other.mean) return false
        if (median != other.median) return false
        if (stdDev != other.stdDev) return false
        if (trend != other.trend) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + encryptedData.contentHashCode()
        result = 31 * result + mean.hashCode()
        result = 31 * result + median.hashCode()
        result = 31 * result + stdDev.hashCode()
        result = 31 * result + trend.hashCode()
        return result
    }
}

/**
 * Data Access Object for analyses
 */
@Dao
interface AnalysisDao {
    @Query("SELECT * FROM analyses ORDER BY timestamp DESC")
    suspend fun getAllAnalyses(): List<AnalysisEntry>
    
    @Query("SELECT * FROM analyses WHERE id = :id")
    suspend fun getAnalysisById(id: Long): AnalysisEntry?
    
    @Insert
    suspend fun insertAnalysis(analysis: AnalysisEntry): Long
    
    @Query("DELETE FROM analyses WHERE id = :id")
    suspend fun deleteAnalysis(id: Long)
    
    @Query("DELETE FROM analyses")
    suspend fun deleteAllAnalyses()
}
