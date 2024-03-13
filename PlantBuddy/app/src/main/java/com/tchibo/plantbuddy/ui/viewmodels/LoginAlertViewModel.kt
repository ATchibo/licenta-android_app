package com.tchibo.plantbuddy.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.tchibo.plantbuddy.controller.backend.MessageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONObject
import javax.inject.Inject

data class LoginAlertState(
    val showNotification: Boolean = false,
    val notificationMessage: String = "",
    val notificationTitle: String = "",
    val onOk: () -> Unit = {},
    val onCancel: () -> Unit = {},
    val connecting: Boolean = false,
    val toastMessage: String = "",
    val loginSuccessful: Boolean = false
)

@HiltViewModel
class LoginAlertViewModel @Inject constructor (

): ViewModel() {

    private val _state = MutableStateFlow(LoginAlertState())
    val state = _state

    private val messageService: MessageService = MessageService(
        onConnected = ::onConnected,
        onDisconnected = ::onDisconnected,
        onFail = ::onFail,
        onMessageReceived = ::onMessageReceived
    )
    
    private var wsCode = ""
    private var logDeviceIn = true;

    init {
        initLoading()
    }

    private fun initLoading() {
        _state.value = _state.value.copy(
            showNotification = false
        )
    }

    fun showNotification(
        title: String,
        message: String,
        data: String,
        onOk: () -> Unit,
        onCancel: () -> Unit
    ) {
        Log.d("LoginRequestViewModel", "showNotification: $title, $message, $data")

        val dataMap: Map<String, String> = Json.decodeFromString(data)
        wsCode = dataMap["wsToken"] ?: ""

        _state.value = _state.value.copy(
            showNotification = true,
            notificationTitle = title,
            notificationMessage = message,
            onOk = onOk,
            onCancel = onCancel
        )
    }

    private fun logDeviceIn() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val email = FirebaseAuth.getInstance().currentUser?.email

        val message: MutableMap<Any?, Any?> = HashMap()
        message["uid"] = uid ?: ""
        message["email"] = email ?: ""

        if (messageService.isConnected.value) {
            messageService.sendMessage(JSONObject(message).toString())
            Log.d("LoginRequestViewModel", "logDeviceIn: sent message")
        } else {
            Log.d("LoginRequestViewModel", "logDeviceIn: not connected")

            _state.value = _state.value.copy(
                connecting = false,
                toastMessage = "Error linking device"
            )
        }
    }

    private fun sendRejectLogIn() {
        val message: MutableMap<Any?, Any?> = HashMap()
        message["message"] = "REJECT_CONN"

        if (messageService.isConnected.value) {
            messageService.sendMessage(JSONObject(message).toString())
            Log.d("LoginRequestViewModel", "sendRejectLogIn: sent message")
        } else {
            Log.d("LoginRequestViewModel", "sendRejectLogIn: not connected")

            _state.value = _state.value.copy(
                connecting = false,
                toastMessage = "Error linking device"
            )
        }
    }

    private fun onMessageReceived(message: String) {
        Log.d("LoginRequestViewModel", "onMessageReceived: $message")

        val msgMap = Json.decodeFromString<Map<String, String>>(message)
        val decodedMessage = msgMap["body"]
        if (decodedMessage == "OK") {
            _state.value = _state.value.copy(
                connecting = false,
                toastMessage = "Linking successful",
                loginSuccessful = true
            )

            messageService.disconnect()
        } else if (decodedMessage == "FAIL") {
            _state.value = _state.value.copy(
                connecting = false,
                toastMessage = "Error linking device"
            )
        }
    }

    private fun onConnected() {
        Log.d("LoginRequestViewModel", "onConnected")

        if (logDeviceIn)
            logDeviceIn()
        else
            sendRejectLogIn()
    }
    private fun onDisconnected() {
        Log.d("LoginRequestViewModel", "onDisconnected")
    }

    private fun onFail(message: String) {
        Log.d("LoginRequestViewModel", "onFail: $message")
    }

    fun onOk() {
        logDeviceIn = true
        CoroutineScope(Dispatchers.IO).launch {
            messageService.connect(wsCode)
            Log.d("LoginRequestViewModel", "logDeviceIn: connected to $wsCode")
        }
        
        _state.value.onOk()
        initLoading()
    }

    fun onCancel() {
        logDeviceIn = false
        CoroutineScope(Dispatchers.IO).launch {
            messageService.connect(wsCode)
            Log.d("LoginRequestViewModel", "dont log device in: connected to $wsCode")
        }

        _state.value.onCancel()
        initLoading()
    }
}