package com.tchibo.plantbuddy.exceptions

import com.google.firebase.firestore.FirebaseFirestoreException

class DeserializationException(
    message: String,
    code: Code
) : FirebaseFirestoreException(message, code) {}