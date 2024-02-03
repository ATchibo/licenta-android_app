package com.tchibo.plantbuddy.domain

data class WateringProgram (
    private val id: String = "",
    private val name: String = "",
    private val frequencyDays: Int = 0,
    private val quantityL: Float = 0.0f,
    private val timeOfDayMin: Int = 0,
    private val minMoisture: Float = 0.0f,
    private val maxMoisture: Float = 0.0f,
) {

    fun fromMap(map: Map<String, Any>): WateringProgram {
        return WateringProgram(
            id = if (map["id"] != null) map["id"] as String else "",
            name = if (map["name"] != null) map["name"] as String else "",
            frequencyDays = if (map["frequencyDays"] != null) (map["frequencyDays"] as Long).toInt() else 0,
            quantityL = if (map["quantityL"] != null) (map["quantityL"] as Double).toFloat() else 0.0f,
            timeOfDayMin = if (map["timeOfDayMin"] != null) (map["timeOfDayMin"] as Long).toInt() else 0,
            minMoisture = if (map["minMoisture"] != null) (map["minMoisture"] as Double).toFloat() else 0.0f,
            maxMoisture = if (map["maxMoisture"] != null) (map["maxMoisture"] as Double).toFloat() else 0.0f,
        )
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "frequencyDays" to frequencyDays,
            "quantityL" to quantityL,
            "timeOfDayMin" to timeOfDayMin,
            "minMoisture" to minMoisture,
            "maxMoisture" to maxMoisture,
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

    fun getId(): String {
        return id
    }

    fun getMinMoisture(): Float {
        return minMoisture
    }

    fun getMaxMoisture(): Float {
        return maxMoisture
    }

    fun toStringBody(): String {
        val hours = timeOfDayMin / 60
        val minutes = timeOfDayMin % 60
        val time =  String.format("%02d:%02d", hours, minutes)

        return "Frequency: once every $frequencyDays days\n" +
                "Time of day: $time\n" +
                "Quantity: $quantityL L\n" +
                "Moisture range: $minMoisture% - $maxMoisture%\n" +
                "Id: $id\n"
    }

    override fun toString(): String {
        return "Name: $name\n" + toStringBody()
    }
}