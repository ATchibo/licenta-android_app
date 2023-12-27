package com.tchibo.plantbuddy.domain

import com.google.firebase.Timestamp

data class MoistureInfoDto (
    val measurementValuePercent: Float,
    val measurementTime: Timestamp,
)