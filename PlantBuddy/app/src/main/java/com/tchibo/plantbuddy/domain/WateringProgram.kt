package com.tchibo.plantbuddy.domain

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.round

data class WateringProgram (
    private val id: String = "",
    private val name: String = "",
    private val frequencyDays: Float = 0.0f,
    private val quantityL: Float = 0.0f,
    private val startingDateTime: Timestamp = Timestamp.now(),
    private val minMoisture: Float = 0.0f,
    private val maxMoisture: Float = 0.0f,
) {

    fun fromMap(map: Map<String, Any>): WateringProgram {
        return WateringProgram(
            id = if (map["id"] != null) map["id"] as String else "",
            name = if (map["name"] != null) map["name"] as String else "",
            frequencyDays = if (map["frequencyDays"] != null) (map["frequencyDays"] as Long).toFloat() else 0.0f,
            quantityL = if (map["quantityL"] != null) (map["quantityL"] as Double).toFloat() else 0.0f,
            startingDateTime = if (map["startingDateTime"] != null) map["startingDateTime"] as Timestamp else Timestamp.now(),
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
            "startingDateTime" to startingDateTime,
            "minMoisture" to minMoisture,
            "maxMoisture" to maxMoisture,
        )
    }

    fun getName(): String {
        return name
    }

    fun getFrequencyDays(): Float {
        return frequencyDays
    }

    fun getQuantityL(): Float {
        return quantityL
    }

    fun getStartingDateTime(): Timestamp {
        return startingDateTime
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
        val time = DateTimeFormatter.ofPattern("MMMM dd, yyyy | hh:mm:ss")
            .format(LocalDateTime.ofInstant(startingDateTime.toDate().toInstant(), ZoneId.systemDefault()))

        val freqDays = round(frequencyDays.toDouble() * 100) / 100

        return "Frequency: once every $freqDays days\n" +
                "Time of day: $time\n" +
                "Quantity: $quantityL L\n" +
                "Moisture range: $minMoisture% - $maxMoisture%"
//                "Id: $id\n"
    }

    override fun toString(): String {
        return "Name: $name\n" + toStringBody()
    }
}