package com.tchibo.plantbuddy.controller.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tchibo.plantbuddy.domain.MoistureInfo
import com.tchibo.plantbuddy.domain.MoistureInfoDao
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.RaspberryInfoDao
import com.tchibo.plantbuddy.utils.DB_NAME
import com.tchibo.plantbuddy.utils.DB_SCHEMA_VERSION
import com.tchibo.plantbuddy.utils.RoomConverters

@Database(entities = [RaspberryInfo::class, MoistureInfo::class], version = DB_SCHEMA_VERSION)
@TypeConverters(RoomConverters::class)
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
                        DB_NAME
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
