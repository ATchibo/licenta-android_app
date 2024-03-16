package com.tchibo.plantbuddy.controller

import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.RaspberryInfoDto

class RaspberryInfoController private constructor(){

    companion object {
        val INSTANCE: RaspberryInfoController by lazy {
            RaspberryInfoController()
        }
    }

    suspend fun getRaspberryInfoDtoList(): List<RaspberryInfoDto> {
        return FirebaseController.INSTANCE.getRaspberryInfoList()
            .map { raspberryInfo ->
                RaspberryInfoDto(
                    raspberryId = raspberryInfo.raspberryId,
                    raspberryName = raspberryInfo.raspberryName,
                )
            }
    }

    suspend fun getRaspberryInfo(rpiId: String): RaspberryInfo? {
        return FirebaseController.INSTANCE.getRaspberryInfo(rpiId)
    }


}