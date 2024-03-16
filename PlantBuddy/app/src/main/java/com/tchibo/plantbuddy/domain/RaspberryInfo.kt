package com.tchibo.plantbuddy.domain

data class RaspberryInfo (
    val raspberryId: String = "",
    val raspberryName: String = "",
    val raspberryLocation: String? = null,
    val raspberryDescription: String? = null,
    val raspberryStatus: RaspberryStatus = RaspberryStatus.NOT_COMPUTED,
) {

    fun setRaspberryId(raspberryId: String): RaspberryInfo {
        return this.copy(raspberryId = raspberryId)
    }

    fun fromMap(map: Map<String, Any>): RaspberryInfo {
        return this.copy(
            raspberryName = map["name"] as String,
            raspberryLocation = map["location"] as String,
            raspberryDescription = map["description"] as String,
        )
    }
}