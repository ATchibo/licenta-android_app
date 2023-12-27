package com.tchibo.plantbuddy.utils

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.Date


class RoomConverters {

    @TypeConverter
    fun fromString(value: String?): Timestamp? {
        return if (value == null) null else Timestamp(
            Date.from(
                java.time.Instant.parse(value)
            )
        )
    }

    @TypeConverter
    fun toString(timestamp: Timestamp?): String? {
        return timestamp?.toDate()?.toInstant()?.toString()
    }
}
