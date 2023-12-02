package com.tchibo.plantbuddy.controller.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.RaspberryInfoDao

@Database(entities = [RaspberryInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun raspberryInfoDao(): RaspberryInfoDao
}
