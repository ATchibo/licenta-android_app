package com.tchibo.plantbuddy.controller.backend

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1

class MessageService(
    onMessageReceived: KFunction1<String, Unit>,
    onConnected: KFunction0<Unit>,
    onDisconnected: KFunction0<Unit>,
    onFail: KFunction1<String, Unit>
) {
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    private val webSocketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            _isConnected.value = true
            onConnected()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            onMessageReceived(text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            _isConnected.value = false
            onDisconnected()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            _isConnected.value = false
            onFail(t.message ?: "Unknown error")
        }
    }

    fun connect(token: String) {
        val webSocketUrl = "ws://82.208.160.92:8181/api/ws/register/$token"

        val request = Request.Builder()
            .url(webSocketUrl)
            .build()

        webSocket = okHttpClient.newBuilder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.SECONDS)
            .build()
            .newWebSocket(request, webSocketListener)
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnected by client")
    }

    fun shutdown() {
        okHttpClient.dispatcher().executorService().shutdown()
    }

    fun sendMessage(text: String) {
        if (_isConnected.value) {
            webSocket?.send(text)
        }
    }
}