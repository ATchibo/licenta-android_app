package com.tchibo.plantbuddy.domain

class WateringInfo (
    private val wateringCommand: String = "",
    private val wateringDuration: String = "0",
    private val wateringVolume: String = "0",
) {

    fun fromMap(map: Map<String, Any>): WateringInfo {

        var waterVolume = "0.0"
        if (map["water_volume"] != null) {
            try {
                waterVolume = (map["water_volume"] as Double).toString()
            } catch (e: Exception) {
                try {
                    waterVolume = (map["water_volume"] as Long).toString()
                } catch (e: Exception) {
                    waterVolume = "0.0"
                }
            }
        }

        return WateringInfo(
            wateringCommand = if (map["command"] != null) map["command"] as String else "0",
            wateringDuration = if (map["watering_duration"] != null)
                (map["watering_duration"] as Long).toString()
                    else "0",
            wateringVolume = waterVolume
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