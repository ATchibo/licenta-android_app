package com.tchibo.plantbuddy.domain

data class SignInResult (
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val id: String,
    val email: String,
    val username: String,
)
