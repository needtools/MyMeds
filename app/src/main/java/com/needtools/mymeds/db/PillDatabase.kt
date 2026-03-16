package com.needtools.mymeds.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.needtools.mymeds.util.PillTypeConverters

@TypeConverters(PillTypeConverters::class)
@Database(
    entities = [Pill::class, PillIntake::class],
    version = 1,
    exportSchema = false
)
abstract class PillDatabase : RoomDatabase() {
    abstract fun pillDao(): PillDao
    abstract fun pillIntakeDao(): PillIntakeDao

    companion object {
        @Volatile
        private var INSTANCE: PillDatabase? = null

        fun getDatabase(context: Context): PillDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PillDatabase::class.java,
                    "pill_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
