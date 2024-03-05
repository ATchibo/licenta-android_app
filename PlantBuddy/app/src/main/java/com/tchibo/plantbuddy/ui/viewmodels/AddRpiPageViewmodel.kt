package com.tchibo.plantbuddy.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.auth.FirebaseAuth
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.controller.backend.MessageService
import com.tchibo.plantbuddy.domain.FirebaseDeviceLinking
import com.tchibo.plantbuddy.domain.UserData
import com.tchibo.plantbuddy.utils.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

data class AddRpiPageState @OptIn(ExperimentalPermissionsApi::class) constructor(
    val isLoading: Boolean = false,
    val processingQrCode: Boolean = false,
    val messageService: MessageService
)

class AddRpiPageViewmodel @OptIn(ExperimentalPermissionsApi::class) constructor(
    private val navigator: NavHostController,
    private val userData: UserData,
): ViewModel() {

    private val _state = mutableStateOf(AddRpiPageState(
        messageService = MessageService(
            onConnected = ::onConnected,
            onDisconnected = ::onDisconnected,
            onFail = ::onFail,
            onMessageReceived = ::onMessageReceived
        )
    ))
    val state: MutableState<AddRpiPageState> = _state

    init {
        initLoading()
    }

    private fun initLoading() {

    }

    private fun linkDevice(firebaseDeviceLinking: FirebaseDeviceLinking) {
        val context = navigator.context
        FirebaseController.INSTANCE.addDeviceAccountLink(firebaseDeviceLinking, context,
            onSuccess = {
                _state.value = _state.value.copy(
                    processingQrCode = false
                )
                navigator.navigate(Routes.getNavigateHome())
            },
            onFailure = {
                _state.value = _state.value.copy(
                    processingQrCode = false
                )
            }
        )
    }

    private fun logDeviceIn(qrCode: String) {
        _state.value.messageService.connect(qrCode)

        Log.d("AddRpiPageViewmodel", "logDeviceIn: connected to $qrCode")

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val email = FirebaseAuth.getInstance().currentUser?.email

        val message: MutableMap<Any?, Any?> = HashMap()
        message["uid"] = uid ?: ""
        message["email"] = email ?: ""

        if (_state.value.messageService.isConnected.value) {
            Log.d("AddRpiPageViewmodel", "logDeviceIn: connected")

            _state.value.messageService.sendMessage(JSONObject(message).toString())
            Log.d("AddRpiPageViewmodel", "logDeviceIn: sent message")

            _state.value.messageService.disconnect()
        } else {
            Log.d("AddRpiPageViewmodel", "logDeviceIn: not connected")
        }
    }

    fun onQrCodeRead(qrCode: String) {
        if (_state.value.processingQrCode || qrCode.isEmpty())
            return

        _state.value = _state.value.copy(
            processingQrCode = true
        )

        CoroutineScope(Dispatchers.IO).launch {
            // log device in
            logDeviceIn(qrCode)

            // adding device to my account
            val firebaseDeviceLinking = FirebaseDeviceLinking(qrCode, userData.email)
//            linkDevice(firebaseDeviceLinking)

            _state.value = _state.value.copy(
                processingQrCode = false
            )
        }
    }

    fun stopProcessingQrCode() {
        _state.value = _state.value.copy(
            processingQrCode = false
        )
    }

    private fun onMessageReceived(message: String) {
        Log.d("AddRpiPageViewmodel", "onMessageReceived: $message")
    }

    private fun onConnected() {
        Log.d("AddRpiPageViewmodel", "onConnected")
    }
    private fun onDisconnected() {
        Log.d("AddRpiPageViewmodel", "onDisconnected")
    }

    private fun onFail(message: String) {
        Log.d("AddRpiPageViewmodel", "onFail: $message")
    }
}