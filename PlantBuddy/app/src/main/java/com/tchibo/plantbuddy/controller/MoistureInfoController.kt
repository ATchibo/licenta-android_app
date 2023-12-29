package com.tchibo.plantbuddy.controller

import com.google.firebase.Timestamp
import com.tchibo.plantbuddy.domain.MoistureInfo

class MoistureInfoController private constructor() {

    companion object {
        val INSTANCE: MoistureInfoController by lazy {
            MoistureInfoController()
        }
    }

    suspend fun getMoistureInfoForRaspId(
        raspberryId: String,
        startTimestamp: Timestamp,
        endTimestamp: Timestamp
    ): List<MoistureInfo?> {
        val moistureInfoList = FirebaseController.INSTANCE.getMoistureInfoForRaspId(
            raspberryId,
            startTimestamp,
            endTimestamp,
        )
        return moistureInfoList.sortedBy { it?.measurementTime }
    }
}