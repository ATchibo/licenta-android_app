package com.tchibo.plantbuddy.controller.db

import android.content.Context
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.domain.MoistureInfo
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.RaspberryInfoDto
import com.tchibo.plantbuddy.repo.MoistureInfoRepo
import com.tchibo.plantbuddy.repo.OfflineRaspberryRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow

class LocalDbController_deprecated private constructor(
    private val raspberryInfoRepo: OfflineRaspberryRepo,
    private val moistureInfoRepo: MoistureInfoRepo,
) {
    companion object {
        private lateinit var applicationContext: Context

        fun initialize(context: Context) {
            applicationContext = context.applicationContext
        }

        val INSTANCE: LocalDbController_deprecated by lazy {
            if (!::applicationContext.isInitialized) {
                throw IllegalStateException("LocalDbController must be initialized with a context")
            }

            val db = AppDatabase.getInstance(applicationContext)

            LocalDbController_deprecated(
                OfflineRaspberryRepo(db.raspberryInfoDao()),
                MoistureInfoRepo(db.moistureInfoDao()),
            )
        }
    }

    suspend fun loadInitialData() {
        println("Loading initial data...")

        loadRaspberryInfoList()
        loadMoistureInfoList()
    }

    private suspend fun setRaspberryInfoList(raspberryInfoList: List<RaspberryInfo>) {
        raspberryInfoRepo.clear()

        for (raspberryInfo in raspberryInfoList) {
            raspberryInfoRepo.insertItem(raspberryInfo)
        }
    }

    private suspend fun setMoistureInfoList(moistureInfoList: List<MoistureInfo>) {
        moistureInfoRepo.clear()

        for (moistureInfo in moistureInfoList) {
            moistureInfoRepo.insertItem(moistureInfo)
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

    suspend fun getMoistureInfoForRaspId(rpiId: String): List<MoistureInfo?> {
        try {
            val moistureInfoFlow = moistureInfoRepo.getItemsWithRaspIdStream(rpiId)
            println("Loaded moisture info flow.")

            // Collect the Flow using collect
            val moistureInfoList = mutableListOf<MoistureInfo?>()
            println("Asteptam sa se termine de colectat datele...")
            moistureInfoFlow.collect { moistureInfo ->
                println("Am primit un moisture info.")
                moistureInfoList.add(moistureInfo)
            }

            println("Loaded moisture info list.")
            return moistureInfoList
        } catch (e: Exception) {
            // Handle exceptions appropriately
            e.printStackTrace()
            return emptyList()
        }
    }


    private suspend fun loadRaspberryInfoList() {
        // load all raspberry info from firebase
        // for each raspberry info, add it to the local db

        val raspberryInfo = FirebaseController.INSTANCE.getRaspberryInfoList()
        setRaspberryInfoList(raspberryInfo)
    }

    private suspend fun loadMoistureInfoList() {
        // load all moisture info from firebase
        // for each moisture info, add it to the local db

        val moistureInfo = FirebaseController.INSTANCE.getMoistureInfoList()
        setMoistureInfoList(moistureInfo)
    }
}
