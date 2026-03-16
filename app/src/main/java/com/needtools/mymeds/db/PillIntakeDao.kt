package com.needtools.mymeds.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PillIntakeDao {
    @Insert
    suspend fun insert(pillIntake: PillIntake)

    @Query("SELECT * FROM pill_intakes WHERE pillId = :pillId ORDER BY intakeTime DESC")
    fun getIntakesForPill(pillId: Int): Flow<List<PillIntake>>

    @Query("SELECT * FROM pill_intakes")
    fun getAllIntakes(): Flow<List<PillIntake>>

    @Query("DELETE FROM pill_intakes")
    suspend fun deleteAllIntakes()

    @Query("DELETE FROM pill_intakes WHERE intakeTime < :timestampThreshold")
    suspend fun deleteOldIntakes(timestampThreshold: Long)

    @Query("SELECT * FROM pill_intakes WHERE pillId = :pillId AND intakeTime >= :startOfDay AND intakeTime <= :endOfDay")
    fun getIntakesForPillToday(pillId: Int, startOfDay: Long, endOfDay: Long): Flow<List<PillIntake>>

}