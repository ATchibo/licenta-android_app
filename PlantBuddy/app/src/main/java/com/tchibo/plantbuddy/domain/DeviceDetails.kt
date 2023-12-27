package com.tchibo.plantbuddy.domain;

data class DeviceDetails (
    val raspberryInfo: RaspberryInfo = RaspberryInfo(),
    val humidityReadings: List<MoistureInfoDto> = mutableListOf(),
)
