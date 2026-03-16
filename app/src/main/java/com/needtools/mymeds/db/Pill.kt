package com.needtools.mymeds.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity(tableName = "pills")
data class Pill(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val dose: String,
    val numberOfIntakes: Int = 1,
    val scheduleTime: List<String>,
    val intakeInstructions: String? = null,
    val form: String,
    val isPermanent: Boolean = false,
    val notes: String? ="",
    val courseDurationDays: Int?,
    val creationDate: Long = Clock.System.now().toEpochMilliseconds(),
    val numberInList: Int = 0
)