package com.tchibo.plantbuddy.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tchibo.plantbuddy.domain.FirebaseDeviceLinking

class FirebaseController private constructor(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private final val deviceLinksCollectionName = "device_links"

    companion object {
        val INSTANCE: FirebaseController by lazy { FirebaseController() }
    }

    fun addDeviceAccountLink(
        firebaseDeviceLinking: FirebaseDeviceLinking,
        context: Context,
        onSuccess: () -> Unit = {},
    ) {
        val dbLinks: CollectionReference = db.collection(deviceLinksCollectionName)

        println("Adauagm link: $firebaseDeviceLinking")

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
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Device already linked to another account",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}