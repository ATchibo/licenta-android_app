package com.tchibo.plantbuddy.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
data class RaspberryInfo (
    @PrimaryKey val raspberryId: String = "",
    @ColumnInfo val raspberryName: String = "",
    @ColumnInfo val raspberryLocation: String? = null,
    @ColumnInfo val raspberryDescription: String? = null,
    @ColumnInfo val raspberryStatus: RaspberryStatus? = RaspberryStatus.OFFLINE,
)