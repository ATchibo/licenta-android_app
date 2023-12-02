package com.tchibo.plantbuddy.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RaspberryInfo (
    @PrimaryKey val raspberryId: String,
    @ColumnInfo val raspberryName: String,
    @ColumnInfo val raspberryLocation: String?,
    @ColumnInfo val raspberryDescription: String?,
    @ColumnInfo val raspberryStatus: RaspberryStatus
)