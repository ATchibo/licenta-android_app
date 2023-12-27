package com.tchibo.plantbuddy.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.type.DateTime

@Entity(primaryKeys = ["raspberryId", "measurementTime"])
data class MoistureInfo (
    @ColumnInfo val raspberryId: Int,
    @ColumnInfo val measurementValuePercent: Int,
    @ColumnInfo val measurementTime: DateTime,
)