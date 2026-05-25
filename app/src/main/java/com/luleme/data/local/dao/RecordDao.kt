package com.luleme.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.luleme.data.local.entity.RecordEntity

data class DailyCount(
    val date: String,
    val count: Int
)

@Dao
interface RecordDao {
    @Query("SELECT * FROM records WHERE date >= :startDate AND date <= :endDate ORDER BY timestamp DESC")
    suspend fun getRecordsBetween(startDate: String, endDate: String): List<RecordEntity>
    
    @Insert
    suspend fun insertRecord(record: RecordEntity): Long

    @Insert
    suspend fun insertRecords(records: List<RecordEntity>)

    @Query(
        """
        UPDATE records
        SET timestamp = :timestamp,
            date = :date,
            note = :note
        WHERE id = :id
        """
    )
    suspend fun updateRecord(id: Long, timestamp: Long, date: String, note: String?)

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteRecord(id: Long)

    @Query("SELECT * FROM records WHERE date = :date ORDER BY timestamp ASC")
    suspend fun getRecordsByDate(date: String): List<RecordEntity>

    @Query(
        """
        SELECT date, COUNT(*) as count
        FROM records
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY date
        """
    )
    suspend fun getDailyCountsBetween(startDate: String, endDate: String): List<DailyCount>
    
    @Query("DELETE FROM records")
    suspend fun clearAll()

    @Query("SELECT * FROM records ORDER BY timestamp DESC")
    suspend fun getAllRecords(): List<RecordEntity>
}
