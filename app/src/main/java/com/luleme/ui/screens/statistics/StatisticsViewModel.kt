package com.quleme.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quleme.domain.model.Record
import com.quleme.domain.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
    private val visibleMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    private val selectedDate = MutableStateFlow<LocalDate?>(null)

    init {
        observeData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            val weekCountsFlow = recordRepository.observeDailyCountsBetween(
                startOfWeek.format(DateTimeFormatter.ISO_DATE),
                endOfWeek.format(DateTimeFormatter.ISO_DATE)
            )

            val monthCountsFlow = visibleMonth.flatMapLatest { month ->
                val monthStart = month.withDayOfMonth(1)
                val monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth())
                recordRepository.observeDailyCountsBetween(
                    monthStart.format(DateTimeFormatter.ISO_DATE),
                    monthEnd.format(DateTimeFormatter.ISO_DATE)
                ).map { counts -> monthStart to counts }
            }

            val selectedRecordsFlow = selectedDate.flatMapLatest { date ->
                if (date == null) {
                    flowOf<Pair<LocalDate?, List<Record>>>(null to emptyList())
                } else {
                    recordRepository.observeRecordsByDate(date.format(DateTimeFormatter.ISO_DATE))
                        .map { records -> date to records }
                }
            }

            try {
                combine(
                    recordRepository.observeTotalCount(),
                    recordRepository.observeRecordDates(),
                    weekCountsFlow,
                    monthCountsFlow,
                    selectedRecordsFlow
                ) { totalCount, recordDates, weekCounts, monthPair, selectedPair ->
                    val (month, monthCounts) = monthPair
                    val (selected, selectedRecords) = selectedPair
                    StatisticsUiState(
                        weekData = buildWeekData(startOfWeek, weekCounts),
                        monthData = buildMonthData(month, monthCounts),
                        visibleMonth = month,
                        selectedDate = selected,
                        selectedDateRecords = selectedRecords,
                        totalCount = totalCount,
                        maxStreak = calculateMaxStreak(recordDates),
                        averageFrequency = calculateAverageFrequency(totalCount, recordDates, today),
                        loading = false
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    fun showPreviousMonth() {
        visibleMonth.value = visibleMonth.value.minusMonths(1)
    }

    fun showNextMonth() {
        visibleMonth.value = visibleMonth.value.plusMonths(1)
    }

    fun showCurrentMonth() {
        visibleMonth.value = LocalDate.now().withDayOfMonth(1)
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    fun clearSelectedDate() {
        selectedDate.value = null
    }

    fun addRecord(timestamp: Long, note: String?) {
        viewModelScope.launch {
            recordRepository.addRecord(timestamp, note)
        }
    }

    fun updateRecord(record: Record) {
        viewModelScope.launch {
            recordRepository.updateRecord(record)
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            recordRepository.deleteRecord(id)
        }
    }

    private fun buildWeekData(startOfWeek: LocalDate, counts: Map<String, Int>): Map<DayOfWeek, Int> {
        return (0..6).associate { offset ->
            val date = startOfWeek.plusDays(offset.toLong())
            date.dayOfWeek to (counts[date.format(DateTimeFormatter.ISO_DATE)] ?: 0)
        }
    }

    private fun buildMonthData(month: LocalDate, counts: Map<String, Int>): Map<LocalDate, Int> {
        return (0 until month.lengthOfMonth()).associate { offset ->
            val date = month.plusDays(offset.toLong())
            date to (counts[date.format(DateTimeFormatter.ISO_DATE)] ?: 0)
        }
    }

    private fun calculateAverageFrequency(totalCount: Int, dates: List<String>, today: LocalDate): Float {
        val firstRecord = dates.firstOrNull() ?: return 0f
        val days = ChronoUnit.DAYS.between(LocalDate.parse(firstRecord), today) + 1
        val weeks = kotlin.math.ceil(days / 7.0).toFloat()
        return totalCount.toFloat() / weeks
    }

    private fun calculateMaxStreak(dates: List<String>): Int {
        if (dates.isEmpty()) return 0
        
        val sortedDates = dates.map { LocalDate.parse(it) }.distinct().sorted()
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
