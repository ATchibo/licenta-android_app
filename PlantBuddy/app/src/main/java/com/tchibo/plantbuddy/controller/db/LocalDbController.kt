package com.tchibo.plantbuddy.controller.db

import android.content.Context
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.RaspberryInfoDto
import com.tchibo.plantbuddy.repo.OfflineRaspberryInfoRepo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

class LocalDbController private constructor(
    private val raspberryInfoRepo: OfflineRaspberryInfoRepo,
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

            val db = AppDatabase.getInstance(applicationContext)

            LocalDbController(
                OfflineRaspberryInfoRepo(db.raspberryInfoDao())
            )
        }
    }

    suspend fun loadInitialData() {
        println("Loading initial data...")

        // load all raspberry info from firebase
        // for each raspberry info, add it to the local db
        val raspberryInfo = FirebaseController.INSTANCE.getRaspberryInfoList()
        setRaspberryInfoList(raspberryInfo)
    }

    private suspend fun setRaspberryInfoList(raspberryInfoList: List<RaspberryInfo>) {
        for (raspberryInfo in raspberryInfoList) {
            raspberryInfoRepo.insertItem(raspberryInfo)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getRaspberryInfoDtoList(): List<RaspberryInfoDto> {
        return raspberryInfoRepo.getAllItemsStream()
            .flatMapConcat { raspberryInfoList ->
                flow {
                    val dtoList = raspberryInfoList.map { raspberryInfo ->
                        RaspberryInfoDto(
                            raspberryInfo.raspberryId,
                            raspberryInfo.raspberryName,
                            raspberryInfo.raspberryStatus
                        )
                    }

                    emit(dtoList)
                }
            }.first()
    }
}
