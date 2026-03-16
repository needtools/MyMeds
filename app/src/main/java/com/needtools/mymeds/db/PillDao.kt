package com.needtools.mymeds.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PillDao {

    @Insert
    suspend fun insert(pill: Pill): Long

    @Query("SELECT * FROM pills ORDER BY numberInList ASC")
    fun getAllPills(): Flow<List<Pill>>

    @Query("SELECT * FROM pills ORDER BY numberInList ASC")
    suspend fun getAllPillsList(): List<Pill>

    @Delete
    suspend fun delete(pill: Pill)

    @Update
    suspend fun update(pill: Pill)

    @Query("SELECT * FROM pills WHERE id = :id")
    fun getPillById(id: Int): Flow<Pill?>

    @Query("SELECT * FROM pills ORDER BY numberInList ASC")
    suspend fun getAllPillsOnce(): List<Pill>

    @Query("SELECT MAX(numberInList) FROM pills")
    suspend fun getMaxNumber(): Int?

    @Query("UPDATE pills SET numberInList = :newPosition WHERE id = :pillId")
    suspend fun updatePillPosition(pillId: Int, newPosition: Int)

    @Query("SELECT COUNT(id) FROM pills")
    suspend fun getPillCount(): Int
}