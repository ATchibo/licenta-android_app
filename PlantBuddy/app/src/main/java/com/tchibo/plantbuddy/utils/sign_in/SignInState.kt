package com.tchibo.plantbuddy.utils.sign_in

data class SignInState (
    val isSignInSuccessful: Boolean = false,
    val errorMessage: String? = null,
)