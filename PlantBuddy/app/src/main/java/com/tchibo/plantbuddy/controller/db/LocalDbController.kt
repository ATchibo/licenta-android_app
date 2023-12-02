package com.tchibo.plantbuddy.controller.db

import android.content.Context
import androidx.room.Room
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.RaspberryInfoDto

class LocalDbController private constructor(
    private val db: AppDatabase,
) {
    companion object {
        private lateinit var applicationContext: Context

        fun initialize(context: Context) {
            applicationContext = context.applicationContext
        }

        val INSTANCE: LocalDbController by lazy {
            if (!::applicationContext.isInitialized) {
                throw IllegalStateException("LocalDbController must be initialized with a context")
            }

            LocalDbController(
                Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "plantbuddy-db"
                ).build()
            )
        }
    }

    fun loadInitialData() {
        println("Loading initial data...")

        // load all raspberry info from firebase
        // for each raspberry info, add it to the local db
        val raspberryInfo = FirebaseController.INSTANCE.getRaspberryInfoList()
        setRaspberryInfoList(raspberryInfo)
    }

    fun setRaspberryInfoList(raspberryInfoList: List<RaspberryInfo>) {
        db.raspberryInfoDao().insertAll(raspberryInfoList)
    }

    fun getRaspberryInfoDtoList(): List<RaspberryInfoDto> {
        return db.raspberryInfoDao().getAllDto()
    }
}
