package com.needtools.mymeds.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "pill_intakes",
    foreignKeys = [
        ForeignKey(
            entity = Pill::class,
            parentColumns = ["id"],
            childColumns = ["pillId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PillIntake(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pillId: Int,
    val intakeTime: Long
)
