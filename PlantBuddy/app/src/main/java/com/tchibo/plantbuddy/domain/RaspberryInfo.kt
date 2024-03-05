package com.tchibo.plantbuddy.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RaspberryInfo (
    @PrimaryKey val raspberryId: String = "",
    @ColumnInfo val raspberryName: String = "",
    @ColumnInfo val raspberryLocation: String? = null,
    @ColumnInfo val raspberryDescription: String? = null,
    @ColumnInfo val raspberryStatus: RaspberryStatus? = RaspberryStatus.OFFLINE,
) {

    fun setRaspberryId(raspberryId: String): RaspberryInfo {
        return this.copy(raspberryId = raspberryId)
    }

    fun fromMap(map: Map<String, Any>): RaspberryInfo {
        return this.copy(
            raspberryName = map["name"] as String,
            raspberryLocation = map["location"] as String,
            raspberryDescription = map["description"] as String,
            raspberryStatus = RaspberryStatus.valueOf(map["status"] as String),
        )
    }
}