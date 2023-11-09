package com.tchibo.plantbuddy.temp

import com.tchibo.plantbuddy.domain.RaspberryDataDto
import com.tchibo.plantbuddy.domain.RaspberryStatus

class TempDb {

    companion object {

        private var raspberryDtoItems: List<RaspberryDataDto> = listOf(
            RaspberryDataDto(
                id = "1",
                nickname = "Raspberry 1",
                status = RaspberryStatus.ONLINE,
            ),
            RaspberryDataDto(
                id = "2",
                nickname = "Raspberry 2",
                status = RaspberryStatus.OFFLINE,
            ),
            RaspberryDataDto(
                id = "3",
                nickname = "Raspberry 3",
                status = RaspberryStatus.ONLINE,
            ),
            RaspberryDataDto(
                id = "4",
                nickname = "Raspberry 4",
                status = RaspberryStatus.ONLINE,
            ),
            RaspberryDataDto(
                id = "5",
                nickname = "Raspberry 5",
                status = RaspberryStatus.UNKNOWN,
            ),
        )

        fun getMyRaspberryDtoItems(): List<RaspberryDataDto> {
            return raspberryDtoItems
        }
    }
}