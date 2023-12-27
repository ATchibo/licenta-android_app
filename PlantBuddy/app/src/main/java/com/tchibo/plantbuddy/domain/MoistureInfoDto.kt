package com.tchibo.plantbuddy.domain

import com.google.type.DateTime

data class MoistureInfoDto (
    val measurementValuePercent: Int,
    val measurementTime: DateTime,
)