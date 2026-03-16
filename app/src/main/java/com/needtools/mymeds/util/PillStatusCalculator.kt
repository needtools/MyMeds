package com.needtools.mymeds.util

import com.needtools.mymeds.db.Pill
import com.needtools.mymeds.db.PillWithStatus
import java.util.Calendar

object PillStatusCalculator {

    fun getMissedTimes(pillWithStatus: PillWithStatus): List<String> {
        val scheduleTimes = pillWithStatus.pill.scheduleTime
        val intakeCountToday = pillWithStatus.intakesToday.size

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val pastScheduledTimes = scheduleTimes.filter { time ->
            val parts = time.split(":")
            val scheduledHour = parts[0].toInt()
            val scheduledMinute = parts[1].toInt()
            scheduledHour < currentHour || (scheduledHour == currentHour && scheduledMinute <= currentMinute)
        }

        return if (intakeCountToday < pastScheduledTimes.size) {
            pastScheduledTimes.drop(intakeCountToday)
        } else {
            emptyList()
        }
    }

    fun getDaysSinceCourseEnded(pill: Pill): Int? {
        if (pill.isPermanent) return null
        val endDate = pill.creationDate + (pill.courseDurationDays?.toLong() ?: 0) * 24 * 60 * 60 * 1000
        val currentTime = System.currentTimeMillis()

        return if (currentTime > endDate) {
            val diff = currentTime - endDate
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } else null
    }
}