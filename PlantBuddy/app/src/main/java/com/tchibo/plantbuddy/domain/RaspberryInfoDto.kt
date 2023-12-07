package com.tchibo.plantbuddy.domain

data class RaspberryInfoDto (
    val raspberryId: String,
    var raspberryName: String,
    var raspberryStatus: RaspberryStatus?
)