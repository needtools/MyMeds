package com.needtools.mymeds.db

import android.content.Context
import com.needtools.mymeds.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PillWithStatus(
    val pill: Pill,
    val intakesToday: List<PillIntake>,
    val lastUpdate: Long

) {
    fun isOverdue( context: Context): Boolean {

        val currentTime = getCurrentTime(context)
        val shouldHaveTakenCount = pill.scheduleTime
            .filter { it.isNotBlank() }
            .map { it.trim() }
            .count { it <= currentTime }
        val result = intakesToday.size < shouldHaveTakenCount
        return result

    }

    private fun getCurrentTime(context : Context): String {
        return SimpleDateFormat(context.getString(R.string.HHmm), Locale.getDefault()).format(Date())
    }
}