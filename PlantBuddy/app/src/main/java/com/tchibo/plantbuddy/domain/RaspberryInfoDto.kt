package com.tchibo.plantbuddy.domain

import kotlinx.serialization.Serializable

@Serializable
data class RaspberryInfoDto (
    val id: String,
    var nickname: String,
    var status: RaspberryStatus
)