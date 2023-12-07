package com.tchibo.plantbuddy.controller

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tchibo.plantbuddy.domain.FirebaseDeviceLinking
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.UserData
import kotlinx.coroutines.tasks.await

class FirebaseController private constructor(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val deviceLinksCollectionName = "device_links"
    private val raspberryInfoCollectionName = "raspberry_info"

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
}