package com.transaction.shared.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlin.time.Clock
import kotlin.time.Instant

object DateUtils {

    private val displayFormatter = LocalDateTime.Format {
        day()
        char(' ')
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        year()
        chars(", ")
        amPmHour()
        char(':')
        minute()
        char(' ')
        amPmMarker("AM", "PM")
    }

    private val dateOnlyFormatter = LocalDateTime.Format {
        day()
        char(' ')
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        year()
    }

    fun formatForDisplay(date: LocalDateTime): String = displayFormatter.format(date)

    fun formatDateOnly(date: LocalDateTime): String = dateOnlyFormatter.format(date)

    fun millisToLocalDate(millis: Long): LocalDate {
        return Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }

    fun startOfDay(date: LocalDate): LocalDateTime = LocalDateTime(date, LocalTime(0, 0, 0))

    fun endOfDay(date: LocalDate): LocalDateTime = LocalDateTime(date, LocalTime(23, 59, 59))
    fun combineDateWithCurrentTime(pickedDate: LocalDate): LocalDateTime {
        val currentTime = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time
        return LocalDateTime(pickedDate, currentTime)
    }
}