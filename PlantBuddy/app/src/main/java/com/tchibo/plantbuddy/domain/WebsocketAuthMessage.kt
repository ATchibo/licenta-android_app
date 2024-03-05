package com.tchibo.plantbuddy.domain

data class WebsocketAuthMessage (
    val type: String,
    val token: String
)