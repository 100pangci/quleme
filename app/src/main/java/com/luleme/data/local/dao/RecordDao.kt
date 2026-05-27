package com.quleme.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.quleme.data.local.entity.RecordEntity
import kotlinx.coroutines.flow.Flow

data class DailyCount(
    val date: String,
    val count: Int
)

@Dao
abstract class RecordDao {
    @Query("SELECT * FROM records WHERE date >= :startDate AND date <= :endDate ORDER BY timestamp DESC")
    abstract suspend fun getRecordsBetween(startDate: String, endDate: String): List<RecordEntity>

    @Query("SELECT * FROM records WHERE date >= :startDate AND date <= :endDate ORDER BY timestamp DESC")
    abstract fun observeRecordsBetween(startDate: String, endDate: String): Flow<List<RecordEntity>>
    
    @Insert
    abstract suspend fun insertRecord(record: RecordEntity): Long

    @Insert
    abstract suspend fun insertRecords(records: List<RecordEntity>)

    @Query(
        """
        UPDATE records
        SET timestamp = :timestamp,
            date = :date,
            note = :note,
            updated_at = :updatedAt
        WHERE id = :id
        """
    )
    abstract suspend fun updateRecord(id: Long, timestamp: Long, date: String, note: String?, updatedAt: Long)

    @Query("DELETE FROM records WHERE id = :id")
    abstract suspend fun deleteRecord(id: Long)

    @Query("SELECT * FROM records WHERE date = :date ORDER BY timestamp ASC")
    abstract suspend fun getRecordsByDate(date: String): List<RecordEntity>

    @Query("SELECT * FROM records WHERE date = :date ORDER BY timestamp ASC")
    abstract fun observeRecordsByDate(date: String): Flow<List<RecordEntity>>

    @Query(
        """
        SELECT date, COUNT(*) as count
        FROM records
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY date
        """
    )
    abstract suspend fun getDailyCountsBetween(startDate: String, endDate: String): List<DailyCount>

    @Query(
        """
        SELECT date, COUNT(*) as count
        FROM records
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY date
        """
    )
    abstract fun observeDailyCountsBetween(startDate: String, endDate: String): Flow<List<DailyCount>>
    
    @Query("DELETE FROM records")
    abstract suspend fun clearAll()

    @Query("SELECT * FROM records ORDER BY timestamp DESC")
    abstract suspend fun getAllRecords(): List<RecordEntity>

    @Query("SELECT COUNT(*) FROM records")
    abstract fun observeTotalCount(): Flow<Int>

    @Query("SELECT DISTINCT date FROM records ORDER BY date ASC")
    abstract fun observeRecordDates(): Flow<List<String>>

    @Transaction
    open suspend fun replaceAll(records: List<RecordEntity>) {
        clearAll()
        if (records.isNotEmpty()) {
            insertRecords(records)
        }
    }
}
