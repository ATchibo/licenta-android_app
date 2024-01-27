package com.tchibo.plantbuddy.domain

class WateringInfo (
    private val wateringCommand: String = "",
    private val wateringDuration: String = "0",
    private val wateringVolume: String = "0",
) {

    fun fromMap(map: Map<String, Any>): WateringInfo {
        return WateringInfo(
            wateringCommand = if (map["command"] != null) map["command"] as String else "0",
            wateringDuration = if (map["watering_duration"] != null)
                (map["watering_duration"] as Long).toString()
                    else "0",
            wateringVolume = if (map["water_volume"] != null)
                (map["water_volume"] as Double).toString()
                    else "0",
        )
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "command" to wateringCommand,
            "watering_duration" to wateringDuration,
            "watering_volume" to wateringVolume,
        )
    }

    fun getWateringCommand(): String {
        return wateringCommand
    }

    fun getWateringDuration(): String {
        return wateringDuration
    }

    fun getWateringVolume(): String {
        return wateringVolume
    }
}