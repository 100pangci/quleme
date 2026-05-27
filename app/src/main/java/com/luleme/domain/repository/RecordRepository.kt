package com.quleme.domain.repository

import com.quleme.domain.model.Record
import kotlinx.coroutines.flow.Flow

interface RecordRepository {
    suspend fun getTodayRecords(): List<Record>
    suspend fun getRecordsBetween(startDate: String, endDate: String): List<Record>
    suspend fun getRecordsByDate(date: String): List<Record>
    suspend fun getDailyCountsBetween(startDate: String, endDate: String): Map<String, Int>
    fun observeRecordsBetween(startDate: String, endDate: String): Flow<List<Record>>
    fun observeRecordsByDate(date: String): Flow<List<Record>>
    fun observeDailyCountsBetween(startDate: String, endDate: String): Flow<Map<String, Int>>
    fun observeTotalCount(): Flow<Int>
    fun observeRecordDates(): Flow<List<String>>
    suspend fun addRecord(note: String? = null): Long
    suspend fun addRecord(timestamp: Long, note: String? = null): Long
    suspend fun updateRecord(record: Record)
    suspend fun deleteRecord(id: Long)
    suspend fun clearAll()
    suspend fun getAllRecords(): List<Record>
    suspend fun replaceAllRecords(records: List<Record>)
}
