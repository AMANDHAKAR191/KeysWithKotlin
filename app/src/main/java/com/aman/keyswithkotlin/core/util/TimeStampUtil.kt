package com.aman.keyswithkotlin.core.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeStampUtil {
    private val timeStampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")

    fun generateTimestamp(): String {
        val current = LocalDateTime.now()
        return current.format(timeStampFormatter)
    }

    fun getDate(timestamp: String): Int {
        // Parse the date string
        val date = LocalDateTime.parse(timestamp, timeStampFormatter)

        // Now you can format the day to your desired pattern
        val outputFormatter = DateTimeFormatter.ofPattern("dd")
        val outputDateString = date.format(outputFormatter)
        return date.format(outputFormatter).toInt()
    }

    fun getMonth(timestamp: String): Int {
        // Parse the date string
        val date = LocalDateTime.parse(timestamp, timeStampFormatter)

        // Now you can format the month to your desired pattern
        val outputFormatter = DateTimeFormatter.ofPattern("MM")
        val outputDateString = date.format(outputFormatter)
        return date.format(outputFormatter).toInt()
    }

    fun getYear(timestamp: String): Int {
        // Parse the date string
        val date = LocalDateTime.parse(timestamp, timeStampFormatter)

        // Now you can format the year to your desired pattern
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy")
        val outputDateString = date.format(outputFormatter)
        return date.format(outputFormatter).toInt()
    }

    fun getTime(timestamp: String): String {
        // Parse the date string
        val date = LocalDateTime.parse(timestamp, timeStampFormatter)

        // Now you can format the time to your desired pattern
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss a")
        val outputDateString = date.format(outputFormatter)
        return date.format(outputFormatter)
    }
}