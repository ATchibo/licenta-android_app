package com.tchibo.plantbuddy.domain;

data class FirebaseDeviceLinking(
    val raspberryId: String,
    val ownerEmail: String,
) {
    fun toJson(): String {
        return "{ \"raspberryId\": \"$raspberryId\", \"ownerEmail\": \"$ownerEmail\" }"
    }

    fun fromJson(json: String): FirebaseDeviceLinking {
        val regex = Regex("\\{ \"raspberryId\": \"(.*)\", \"ownerEmail\": \"(.*)\" \\}")
        val matchResult = regex.find(json)
        val (raspberryId, ownerEmail) = matchResult!!.destructured
        return FirebaseDeviceLinking(raspberryId, ownerEmail)
    }
}
