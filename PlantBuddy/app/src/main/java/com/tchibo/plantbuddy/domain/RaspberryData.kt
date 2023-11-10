package com.tchibo.plantbuddy.domain

data class RaspberryData(
    val id: String,
    var nickname: String,
    var status: RaspberryStatus
)