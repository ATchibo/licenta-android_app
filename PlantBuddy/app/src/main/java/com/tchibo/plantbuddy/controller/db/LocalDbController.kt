package com.tchibo.plantbuddy.controller.db

import android.content.Context
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.RaspberryInfoDto
import com.tchibo.plantbuddy.repo.OfflineRaspberryInfoRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow

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

        loadRaspberryInfoList()
    }

    private suspend fun setRaspberryInfoList(raspberryInfoList: List<RaspberryInfo>) {
        raspberryInfoRepo.clear()

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

    suspend fun refreshRaspberryInfoDtoList(): List<RaspberryInfoDto> {
        loadRaspberryInfoList()
        return getRaspberryInfoDtoList()
    }

    suspend fun getRaspberryInfo(rpiId: String): RaspberryInfo? {
        return raspberryInfoRepo.getItemStream(rpiId).first()
    }

    private suspend fun loadRaspberryInfoList() {
        // load all raspberry info from firebase
        // for each raspberry info, add it to the local db

        val raspberryInfo = FirebaseController.INSTANCE.getRaspberryInfoList()
        setRaspberryInfoList(raspberryInfo)
    }
}
