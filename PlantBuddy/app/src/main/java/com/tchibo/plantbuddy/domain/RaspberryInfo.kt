package com.tchibo.plantbuddy.domain

data class RaspberryInfo (
    val raspberryId: String = "",
    val raspberryName: String = "",
    val raspberryLocation: String? = null,
    val raspberryDescription: String? = null,
    val notifiableMessages: HashMap<String, Boolean> = hashMapOf(),
    val raspberryStatus: RaspberryStatus = RaspberryStatus.NOT_COMPUTED,
) {

    fun fromMap(map: Map<String, Any>): RaspberryInfo {
        return this.copy(
            raspberryId = map["id"] as String,
            raspberryName = map["name"] as String,
            raspberryLocation = map["location"] as String,
            raspberryDescription = map["description"] as String,
            notifiableMessages = map["notifiable_messages"] as HashMap<String, Boolean> ,
        )
    }
}