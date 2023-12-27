package com.tchibo.plantbuddy.controller

import com.tchibo.plantbuddy.domain.MoistureInfo

class MoistureInfoController private constructor() {

    companion object {
        val INSTANCE: MoistureInfoController by lazy {
            MoistureInfoController()
        }
    }

    suspend fun getMoistureInfoForRaspId(raspberryId: String): List<MoistureInfo?> {
        val moistureInfoList = FirebaseController.INSTANCE.getMoistureInfoForRaspId(raspberryId)
        println("Moisture info list: $moistureInfoList")
        return moistureInfoList
    }
}