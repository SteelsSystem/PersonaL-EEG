package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room @Database class for SY4-EEG BlockMind ('SPECTRoom'), managing local persistence
 * for inverted SPECT telemetry, biometric drift logs, SHA-256 integrity checks, and Unicritic verdicts.
 */
@Database(
    entities = [
        SpectStatEntity::class,
        SpectRoomEntity::class,
        ScriptVerificationEntity::class,
        UnicriticVerdictEntity::class,
        UnicriticDriftLogEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class SpectDatabase : RoomDatabase() {
    abstract fun spectDao(): SpectDao
    abstract fun spectRoomDao(): SpectRoomDao

    companion object {
        private const val DATABASE_NAME = "sy4_eeg_spect_local.db"

        @Volatile
        private var INSTANCE: SpectDatabase? = null

        fun getInstance(context: Context): SpectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpectDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

typealias SPECTRoomDatabase = SpectDatabase
