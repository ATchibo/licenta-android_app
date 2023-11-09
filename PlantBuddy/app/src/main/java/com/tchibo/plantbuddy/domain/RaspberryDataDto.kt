package com.tchibo.plantbuddy.domain

import kotlinx.serialization.Serializable

@Serializable
data class RaspberryDataDto (
    val id: String,
    var nickname: String,
    var status: RaspberryStatus
)