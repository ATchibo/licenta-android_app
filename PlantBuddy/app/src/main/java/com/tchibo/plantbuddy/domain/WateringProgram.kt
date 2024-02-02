package com.tchibo.plantbuddy.domain

data class WateringProgram (
    private val name: String = "",
    private val frequencyDays: Int = 0,
    private val quantityL: Float = 0.0f,
    private val timeOfDayMin: Int = 0,
) {

    fun fromMap(map: Map<String, Any>): WateringProgram {
        return WateringProgram(
            name = if (map["name"] != null) map["name"] as String else "",
            frequencyDays = if (map["frequency_days"] != null) (map["frequency_days"] as Long).toInt() else 0,
            quantityL = if (map["quantity_l"] != null) (map["quantity_l"] as Double).toFloat() else 0.0f,
            timeOfDayMin = if (map["time_of_day_min"] != null) (map["time_of_day_min"] as Long).toInt() else 0,
        )
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "frequency_days" to frequencyDays,
            "quantity_l" to quantityL,
            "time_of_day_min" to timeOfDayMin,
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