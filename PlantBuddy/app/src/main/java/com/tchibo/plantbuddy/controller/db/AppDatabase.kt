package com.tchibo.plantbuddy.controller.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tchibo.plantbuddy.domain.MoistureInfoDao
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.RaspberryInfoDao

@Database(entities = [RaspberryInfo::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun raspberryInfoDao(): RaspberryInfoDao
    abstract fun moistureInfoDao(): MoistureInfoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // only one thread of execution at a time can enter this block of code
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "plantbuddy_db"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
