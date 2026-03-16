package com.needtools.mymeds.util

import android.content.Context
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun getHistoryDays(): Int = prefs.getInt("history_days", 30)

    fun saveHistoryDays(days: Int) {
        prefs.edit { putInt("history_days", days) }
    }

    fun isSoundEnabled(): Boolean = prefs.getBoolean("is_sound_enabled", true)

    fun saveSoundEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("is_sound_enabled", enabled) }
    }
}