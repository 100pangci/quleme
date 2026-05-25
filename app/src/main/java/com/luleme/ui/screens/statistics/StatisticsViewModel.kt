package com.luleme.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luleme.domain.model.Record
import com.luleme.domain.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class StatisticsUiState(
    val weekData: Map<DayOfWeek, Int> = emptyMap(),
    val monthData: Map<LocalDate, Int> = emptyMap(),
    val visibleMonth: LocalDate = LocalDate.now().withDayOfMonth(1),
    val selectedDate: LocalDate? = null,
    val selectedDateRecords: List<Record> = emptyList(),
    val totalCount: Int = 0,
    val maxStreak: Int = 0,
    val averageFrequency: Float = 0f,
    val loading: Boolean = false
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState(loading = true))
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData(month: LocalDate = _uiState.value.visibleMonth) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            
            val allRecords = recordRepository.getAllRecords()
            val today = LocalDate.now()

            // Week Data
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            val weekCounts = recordRepository.getDailyCountsBetween(
                startOfWeek.format(DateTimeFormatter.ISO_DATE),
                endOfWeek.format(DateTimeFormatter.ISO_DATE)
            )
            val weekData = mutableMapOf<DayOfWeek, Int>()
            for (i in 0..6) {
                val date = startOfWeek.plusDays(i.toLong())
                weekData[date.dayOfWeek] = weekCounts[date.format(DateTimeFormatter.ISO_DATE)] ?: 0
            }

            // Month Data
            val visibleMonth = month.withDayOfMonth(1)
            val startOfMonth = visibleMonth.with(TemporalAdjusters.firstDayOfMonth())
            val endOfMonth = visibleMonth.with(TemporalAdjusters.lastDayOfMonth())
            val monthCounts = recordRepository.getDailyCountsBetween(
                startOfMonth.format(DateTimeFormatter.ISO_DATE),
                endOfMonth.format(DateTimeFormatter.ISO_DATE)
            )
            val monthData = mutableMapOf<LocalDate, Int>()
            for (i in 0 until visibleMonth.lengthOfMonth()) {
                val date = startOfMonth.plusDays(i.toLong())
                monthData[date] = monthCounts[date.format(DateTimeFormatter.ISO_DATE)] ?: 0
            }

            // All Time Stats
            val totalCount = allRecords.size
            val maxStreak = calculateMaxStreak(allRecords)
            
            val firstRecord = allRecords.minByOrNull { it.timestamp }
            val average = if (firstRecord != null) {
                val days = ChronoUnit.DAYS.between(LocalDate.parse(firstRecord.date), today) + 1
                val weeks = kotlin.math.ceil(days / 7.0).toFloat()
                totalCount.toFloat() / weeks
            } else {
                0f
            }

            val selectedDate = _uiState.value.selectedDate
            val selectedRecords = selectedDate?.let {
                recordRepository.getRecordsByDate(it.format(DateTimeFormatter.ISO_DATE))
            }.orEmpty()

            _uiState.value = StatisticsUiState(
                weekData = weekData,
                monthData = monthData,
                visibleMonth = visibleMonth,
                selectedDate = selectedDate,
                selectedDateRecords = selectedRecords,
                totalCount = totalCount,
                maxStreak = maxStreak,
                averageFrequency = average,
                loading = false
            )
        }
    }

    fun showPreviousMonth() {
        loadData(_uiState.value.visibleMonth.minusMonths(1))
    }

    fun showNextMonth() {
        loadData(_uiState.value.visibleMonth.plusMonths(1))
    }

    fun showCurrentMonth() {
        loadData(LocalDate.now())
    }

    fun selectDate(date: LocalDate) {
        viewModelScope.launch {
            val records = recordRepository.getRecordsByDate(date.format(DateTimeFormatter.ISO_DATE))
            _uiState.value = _uiState.value.copy(
                selectedDate = date,
                selectedDateRecords = records
            )
        }
    }

    fun clearSelectedDate() {
        _uiState.value = _uiState.value.copy(
            selectedDate = null,
            selectedDateRecords = emptyList()
        )
    }

    fun addRecord(timestamp: Long, note: String?) {
        viewModelScope.launch {
            recordRepository.addRecord(timestamp, note)
            loadData()
        }
    }

    fun updateRecord(record: Record) {
        viewModelScope.launch {
            recordRepository.updateRecord(record)
            loadData()
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            recordRepository.deleteRecord(id)
            loadData()
        }
    }

    private fun calculateMaxStreak(records: List<Record>): Int {
        if (records.isEmpty()) return 0
        
        val sortedDates = records.map { LocalDate.parse(it.date) }.distinct().sorted()
        var maxStreak = 0
        var currentStreak = 0
        
        // This logic calculates consecutive days with records. 
        // Note: Requirements say "Longest continuous record days", assuming it means days with at least one record.
        // However, for this app, maybe streak means "days WITHOUT record"? 
        // Usually streaks in habit trackers are for "doing the habit". 
        // But here, maybe "not doing it" is the goal? 
        // Requirements say "Core concept: Scientific management". 
        // Let's assume streak means "Consecutive days with records" for now as per standard definition,
        // although "No Fap" apps usually track days *without*.
        // The requirements don't explicitly say "No Fap". It says "Manage frequency".
        // "Longest continuous record days" (最长连续记录天数) implies days WITH records.
        
        for (i in 0 until sortedDates.size) {
            if (i == 0) {
                currentStreak = 1
            } else {
                val prev = sortedDates[i - 1]
                val curr = sortedDates[i]
                if (ChronoUnit.DAYS.between(prev, curr) == 1L) {
                    currentStreak++
                } else {
                    maxStreak = maxOf(maxStreak, currentStreak)
                    currentStreak = 1
                }
            }
        }
        maxStreak = maxOf(maxStreak, currentStreak)
        
        return maxStreak
    }
}
