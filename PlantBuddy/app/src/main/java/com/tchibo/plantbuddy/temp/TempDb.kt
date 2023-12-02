package com.tchibo.plantbuddy.temp

import com.tchibo.plantbuddy.domain.RaspberryInfoDto
import com.tchibo.plantbuddy.domain.RaspberryStatus

class TempDb {

    companion object {

        private var raspberryDtoItems: List<RaspberryInfoDto> = listOf(
            RaspberryInfoDto(
                id = "1",
                nickname = "Raspberry 1",
                status = RaspberryStatus.ONLINE,
            ),
            RaspberryInfoDto(
                id = "2",
                nickname = "Raspberry 2",
                status = RaspberryStatus.OFFLINE,
            ),
            RaspberryInfoDto(
                id = "3",
                nickname = "Raspberry 3",
                status = RaspberryStatus.ONLINE,
            ),
            RaspberryInfoDto(
                id = "4",
                nickname = "Raspberry 4",
                status = RaspberryStatus.ONLINE,
            ),
            RaspberryInfoDto(
                id = "5",
                nickname = "Raspberry 5",
                status = RaspberryStatus.UNKNOWN,
            ),
        )

        fun getMyRaspberryDtoItems(): List<RaspberryInfoDto> {
            return raspberryDtoItems
        }
    }
}