package com.tchibo.plantbuddy.controller

import android.content.Context
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.tchibo.plantbuddy.domain.FirebaseDeviceLinking
import com.tchibo.plantbuddy.domain.MoistureInfo
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.UserData
import com.tchibo.plantbuddy.domain.WateringProgram
import com.tchibo.plantbuddy.exceptions.DeserializationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlin.reflect.KFunction2

class FirebaseController private constructor(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val ownerInfoCollectionName = "owner_info"
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
        val dbLinks: CollectionReference = db.collection(ownerInfoCollectionName)

        try {
            dbLinks.whereArrayContains("raspberry_ids", firebaseDeviceLinking.raspberryId)
                .get()
                .addOnSuccessListener { it ->
                    if (it.isEmpty) {
                        dbLinks.document(firebaseDeviceLinking.ownerEmail)
                            .set(
                                hashMapOf(
                                    "raspberry_ids" to firebaseDeviceLinking.raspberryId
                                ) as Map<String, String>,
                                SetOptions.merge()
                            )
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Device linked successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Fail to link device: \n$e", Toast.LENGTH_SHORT)
                                    .show()

                                onFailure()
                            }

                    } else {
                        if (it.documents.size > 1)
                            throw IllegalStateException("More than one document found for the same raspberry id")

                        if (it.documents[0].id == firebaseDeviceLinking.ownerEmail) {
                            Toast.makeText(
                                context,
                                "Device already linked to this account",
                                Toast.LENGTH_SHORT
                            ).show()

                            onSuccess()
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
        } catch (e: Exception) {
            Toast.makeText(context, "Fail to link device: \n$e", Toast.LENGTH_SHORT)
                .show()
            onFailure()
        }
    }

    suspend fun getRaspberryInfoList(): List<RaspberryInfo> {
        val raspberryIds = db.collection(ownerInfoCollectionName)
            .document(userData.email)
            .get()
            .await()
            .get("raspberry_ids") as List<*>

        return try {
            runBlocking {
                raspberryIds.map { raspId ->
                    async {
                        val raspInfo = db.collection(raspberryInfoCollectionName)
                            .document(raspId.toString())
                            .get()
                            .await()
                            .data

                        if (raspInfo == null)
                            return@async null

                        RaspberryInfo(raspId.toString())
                            .fromMap(raspInfo as Map<String, Any>)
                    }
                }.awaitAll().filterNotNull()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRaspberryInfo(raspberryId: String): RaspberryInfo? {
            return try {
                val raspInfo = db.collection(raspberryInfoCollectionName)
                    .document(raspberryId)
                    .get()
                    .await()
                    .data

                if (raspInfo == null)
                    return null

                RaspberryInfo(raspberryId)
                    .fromMap(raspInfo as Map<String, Any>)

            } catch (e: Exception) {
                null
            }
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

    suspend fun getIsWateringProgramsActive(raspberryId: String): Boolean {
        return db.collection(wateringProgramsCollectionName)
            .document(raspberryId)
            .get()
            .await()
            .get("wateringProgramsEnabled")
            .toString()
            .toBoolean()
    }

    fun setIsWateringProgramsActive(raspberryId: String, isActive: Boolean) {
        db.collection(wateringProgramsCollectionName)
            .document(raspberryId)
            .update(
                hashMapOf(
                    "wateringProgramsEnabled" to isActive
                ) as Map<String, Any>
            )
    }

    fun addWateringProgram(
        raspberryId: String,
        wateringProgram: WateringProgram,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        db.collection(wateringProgramsCollectionName)
            .document(raspberryId)
            .collection(wateringProgramsCollectionNestedCollectionName)
            .add(wateringProgram)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }
}