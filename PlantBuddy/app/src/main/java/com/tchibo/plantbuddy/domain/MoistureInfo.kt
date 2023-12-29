package com.tchibo.plantbuddy.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.firebase.Timestamp

@Entity(primaryKeys = ["raspberryId", "measurementTime"])
data class MoistureInfo (
    @ColumnInfo val raspberryId: String = "",
    @ColumnInfo val measurementValuePercent: Float = 0.0f,
    @ColumnInfo val measurementTime: Timestamp = Timestamp.now(),
)