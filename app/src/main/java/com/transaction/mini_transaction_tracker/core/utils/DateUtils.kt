package com.transaction.mini_transaction_tracker.core.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateUtils {
    private val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
    private val dateOnlyFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    fun formatForDisplay(date: LocalDateTime): String = date.format(displayFormatter)

    fun formatDateOnly(date: LocalDateTime): String = date.format(dateOnlyFormatter)
    fun millisToLocalDate(millis: Long): LocalDate {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
    }

    fun combineDateWithCurrentTime(pickedDate: LocalDate): LocalDateTime {
        return LocalDateTime.of(pickedDate, LocalTime.now())
    }
}