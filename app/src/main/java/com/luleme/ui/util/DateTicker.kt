package com.quleme.ui.util

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime

fun observeLocalDates(): Flow<LocalDate> = flow {
    while (currentCoroutineContext().isActive) {
        emit(LocalDate.now())
        delay(millisUntilNextLocalDate())
    }
}.distinctUntilChanged()

private fun millisUntilNextLocalDate(): Long {
    val now = ZonedDateTime.now()
    val nextDate = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
    return Duration.between(now, nextDate).toMillis().coerceAtLeast(1_000L)
}
