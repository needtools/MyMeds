package com.needtools.mymeds.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.needtools.mymeds.db.PillDao
import com.needtools.mymeds.db.PillIntakeDao

class MedsViewModelFactory(
    private val pillDao: PillDao,
    private val intakeDao: PillIntakeDao,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedsViewModel(pillDao, intakeDao, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}