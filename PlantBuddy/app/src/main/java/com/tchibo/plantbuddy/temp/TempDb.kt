package com.tchibo.plantbuddy.temp

import com.tchibo.plantbuddy.domain.RaspberryInfoDto
import com.tchibo.plantbuddy.domain.RaspberryStatus

class TempDb {

    companion object {

        private var raspberryDtoItems: List<RaspberryInfoDto> = listOf(
            RaspberryInfoDto(
                raspberryId = "1",
                raspberryName = "Raspberry 1",
                raspberryStatus = RaspberryStatus.ONLINE,
            ),
            RaspberryInfoDto(
                raspberryId = "2",
                raspberryName = "Raspberry 2",
                raspberryStatus = RaspberryStatus.OFFLINE,
            ),
            RaspberryInfoDto(
                raspberryId = "3",
                raspberryName = "Raspberry 3",
                raspberryStatus = RaspberryStatus.ONLINE,
            ),
            RaspberryInfoDto(
                raspberryId = "4",
                raspberryName = "Raspberry 4",
                raspberryStatus = RaspberryStatus.ONLINE,
            ),
            RaspberryInfoDto(
                raspberryId = "5",
                raspberryName = "Raspberry 5",
                raspberryStatus = RaspberryStatus.UNKNOWN,
            ),
        )

        fun getMyRaspberryDtoItems(): List<RaspberryInfoDto> {
            return raspberryDtoItems
        }
    }
}