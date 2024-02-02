package com.tchibo.plantbuddy.domain

data class WateringProgram (
    private val id: String = "",
    private val name: String = "",
    private val frequencyDays: Int = 0,
    private val quantityL: Float = 0.0f,
    private val timeOfDayMin: Int = 0,
) {

    fun fromMap(map: Map<String, Any>): WateringProgram {
        return WateringProgram(
            id = if (map["id"] != null) map["id"] as String else "",
            name = if (map["name"] != null) map["name"] as String else "",
            frequencyDays = if (map["frequencyDays"] != null) (map["frequencyDays"] as Long).toInt() else 0,
            quantityL = if (map["quantityL"] != null) (map["quantityL"] as Double).toFloat() else 0.0f,
            timeOfDayMin = if (map["timeOfDayMin"] != null) (map["timeOfDayMin"] as Long).toInt() else 0,
        )
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "frequencyDays" to frequencyDays,
            "quantityL" to quantityL,
            "timeOfDayMin" to timeOfDayMin,
        )
    }

    fun getName(): String {
        return name
    }

    fun getFrequencyDays(): Int {
        return frequencyDays
    }

    fun getQuantityL(): Float {
        return quantityL
    }

    fun getTimeOfDayMin(): Int {
        return timeOfDayMin
    }
}