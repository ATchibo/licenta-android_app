package com.tchibo.plantbuddy.controller

import android.content.Context
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.tchibo.plantbuddy.domain.FirebaseDeviceLinking
import com.tchibo.plantbuddy.domain.MoistureInfo
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.UserData
import com.tchibo.plantbuddy.domain.WateringProgram
import com.tchibo.plantbuddy.exceptions.DeserializationException
import kotlinx.coroutines.tasks.await
import kotlin.reflect.KFunction2

class FirebaseController private constructor(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val deviceLinksCollectionName = "device_links"
    private val raspberryInfoCollectionName = "raspberry_info"
    private val moistureInfoCollectionName = "humidity_readings"
    private val wateringNowCollectionName = "watering_info"
    private val wateringProgramsCollectionName = "watering_programs"
    private val wateringProgramsCollectionNestedCollectionName = "programs"
    private val globalWateringProgramsCollectionName = "global_watering_programs"

    companion object {
        private lateinit var userData: UserData
        fun initialize(userData: UserData) {
            FirebaseController.userData = userData
        }

        val INSTANCE: FirebaseController by lazy {
            if (!::userData.isInitialized) {
                throw IllegalStateException("FirebaseController must be initialized with a user data")
            }

            FirebaseController()
        }
    }

    fun addDeviceAccountLink(
        firebaseDeviceLinking: FirebaseDeviceLinking,
        context: Context,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val dbLinks: CollectionReference = db.collection(deviceLinksCollectionName)

        dbLinks.whereEqualTo("raspberryId", firebaseDeviceLinking.raspberryId)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    dbLinks.add(firebaseDeviceLinking).addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Device linked successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        onSuccess()
                    }.addOnFailureListener { e ->
                        Toast.makeText(context, "Fail to link device: \n$e", Toast.LENGTH_SHORT)
                            .show()

                        onFailure()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Device already linked to another account",
                        Toast.LENGTH_SHORT
                    ).show()

                    onFailure()
                }
            }
    }

    suspend fun getRaspberryInfoList(): List<RaspberryInfo> {
        val raspberryIds = db.collection(deviceLinksCollectionName)
            .whereEqualTo("ownerEmail", userData.email)
            .get()
            .await()
            .map { it -> it.get("raspberryId") }

        return db.collection(raspberryInfoCollectionName)
            .whereIn("raspberryId", raspberryIds)
            .get()
            .await()
            .toObjects(RaspberryInfo::class.java)
    }

    suspend fun getRaspberryInfo(raspberryId: String): RaspberryInfo? {
        return db.collection(raspberryInfoCollectionName)
            .whereEqualTo("raspberryId", raspberryId)
            .get()
            .await()
            .toObjects(RaspberryInfo::class.java)
            .firstOrNull()
    }

    suspend fun getMoistureInfoList(): List<MoistureInfo> {
        val raspberryIds = db.collection(deviceLinksCollectionName)
            .whereEqualTo("ownerEmail", userData.email)
            .get()
            .await()
            .map { it -> it.get("raspberryId") }

        return db.collection("moisture_info")
            .whereIn("raspberryId", raspberryIds)
            .get()
            .await()
            .toObjects(MoistureInfo::class.java)
    }

    suspend fun getMoistureInfoForRaspId(
        rpiId: String,
        startTimestamp: Timestamp,
        endTimestamp: Timestamp
    ): List<MoistureInfo?> {
        return db.collection(moistureInfoCollectionName)
            .whereEqualTo("raspberryId", rpiId)
            .whereGreaterThanOrEqualTo("measurementTime", startTimestamp)
            .whereLessThanOrEqualTo("measurementTime", endTimestamp)
            .get()
            .await()
            .toObjects(MoistureInfo::class.java)
    }

    fun createListenerForWateringNow(
        raspberryId: String,
        callback: KFunction2<DocumentSnapshot?, FirebaseFirestoreException?, Unit>
    ): ListenerRegistration {

        return db.collection(wateringNowCollectionName)
            .document(raspberryId)
            .addSnapshotListener { snapshot, e ->
                callback(snapshot, e)
            }
    }

    fun startWatering(raspberryId: String) {
        db.collection(wateringNowCollectionName)
            .document(raspberryId)
            .update(
                hashMapOf(
                    "command" to "start_watering"
                ) as Map<String, Any>
            )
    }

    fun stopWatering(raspberryId: String) {
        db.collection(wateringNowCollectionName)
            .document(raspberryId)
            .update(
                hashMapOf(
                    "command" to "stop_watering"
                ) as Map<String, Any>
            )
    }


    suspend fun getWateringPrograms(raspberryId: String): List<WateringProgram> {
        val wateringPrograms = db.collection(wateringProgramsCollectionName)
            .document(raspberryId)
            .collection(wateringProgramsCollectionNestedCollectionName)
            .get()
            .await()
            .documents.map { documentSnapshot ->
                val wateringProgram = documentSnapshot.toObject(WateringProgram::class.java)
                    ?: throw DeserializationException(
                        "Error deserializing watering program document",
                        FirebaseFirestoreException.Code.ABORTED
                    )
                wateringProgram.copy(id = documentSnapshot.id)
            }
            .toMutableList()

        val globalWateringPrograms = db.collection(globalWateringProgramsCollectionName)
            .get()
            .await()
            .documents.map { documentSnapshot ->
                val wateringProgram = documentSnapshot.toObject(WateringProgram::class.java)
                    ?: throw DeserializationException(
                        "Error deserializing watering program document",
                        FirebaseFirestoreException.Code.ABORTED
                    )
                wateringProgram.copy(id = documentSnapshot.id)
            }

        wateringPrograms.addAll(globalWateringPrograms)

        return wateringPrograms
    }

    suspend fun getActiveWateringProgramId(raspberryId: String): String {
        return db.collection(wateringProgramsCollectionName)
            .document(raspberryId)
            .get()
            .await()
            .get("activeProgramId")
            .toString()
    }

    fun setActiveWateringProgramId(raspberryId: String, programId: String) {
        db.collection(wateringProgramsCollectionName)
            .document(raspberryId)
            .update(
                hashMapOf(
                    "activeProgramId" to programId
                ) as Map<String, Any>
            )
    }
}