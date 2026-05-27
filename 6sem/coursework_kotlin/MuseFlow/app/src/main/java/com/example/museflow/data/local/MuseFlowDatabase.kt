package com.example.museflow.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.museflow.data.local.dao.TrackDao
import com.example.museflow.data.local.entities.TrackEntity

@Database(
    entities = [TrackEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MuseFlowDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    companion object {
        @Volatile
        private var INSTANCE: MuseFlowDatabase? = null

        fun getInstance(context: Context): MuseFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MuseFlowDatabase::class.java,
                    "museflow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}