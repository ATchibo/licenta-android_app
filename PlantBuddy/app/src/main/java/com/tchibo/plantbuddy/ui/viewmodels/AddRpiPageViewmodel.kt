package com.tchibo.plantbuddy.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.tchibo.plantbuddy.controller.backend.MessageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONObject

data class AddRpiPageState constructor(
    val isLoading: Boolean = false,
    val processingQrCode: Boolean = false,
    val messageService: MessageService,
    val toastMessage: String? = null,
    val loginSuccessful: Boolean? = null
)

class AddRpiPageViewmodel (
    private val navigator: NavHostController,
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

    private var wsCode: String = ""

    init {
        initLoading()
    }

    private fun initLoading() {

    }

//    private fun linkDevice(firebaseDeviceLinking: FirebaseDeviceLinking) {
//        val context = navigator.context
//        FirebaseController.INSTANCE.addDeviceAccountLink(firebaseDeviceLinking, context,
//            onSuccess = {
//                _state.value = _state.value.copy(
//                    processingQrCode = false
//                )
//                navigator.navigate(Routes.getNavigateHome())
//            },
//            onFailure = {
//                _state.value = _state.value.copy(
//                    processingQrCode = false
//                )
//            }
//        )
//    }

    private fun logDeviceIn() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val email = FirebaseAuth.getInstance().currentUser?.email

        val message: MutableMap<Any?, Any?> = HashMap()
        message["uid"] = uid ?: ""
        message["email"] = email ?: ""

        if (_state.value.messageService.isConnected.value) {
            _state.value.messageService.sendMessage(JSONObject(message).toString())
            Log.d("AddRpiPageViewmodel", "logDeviceIn: sent message")
        } else {
            Log.d("AddRpiPageViewmodel", "logDeviceIn: not connected")

            _state.value = _state.value.copy(
                processingQrCode = false,
                toastMessage = "Error linking device"
            )
        }
    }

    fun onQrCodeRead(qrCode: String) {
        if (_state.value.processingQrCode || qrCode.isEmpty())
            return

        _state.value = _state.value.copy(
            processingQrCode = true
        )

        CoroutineScope(Dispatchers.IO).launch {
            wsCode = qrCode
            _state.value.messageService.connect(qrCode)
            Log.d("AddRpiPageViewmodel", "logDeviceIn: connected to $qrCode")
        }
    }

    fun stopProcessingQrCode() {
        _state.value = _state.value.copy(
            processingQrCode = false
        )
    }

    private fun onMessageReceived(message: String) {
        Log.d("AddRpiPageViewmodel", "onMessageReceived: $message")

        val msgMap = Json.decodeFromString<Map<String, String>>(message)
        val decodedMessage = msgMap["body"]
        if (decodedMessage == "OK") {
            _state.value = _state.value.copy(
                processingQrCode = false,
                toastMessage = "Linking successful",
                loginSuccessful = true
            )

            _state.value.messageService.disconnect()
        } else if (decodedMessage == "FAIL") {
            _state.value = _state.value.copy(
                processingQrCode = false,
                toastMessage = "Error linking device"
            )
        }
    }

    private fun onConnected() {
        Log.d("AddRpiPageViewmodel", "onConnected")
        logDeviceIn()
    }
    private fun onDisconnected() {
        Log.d("AddRpiPageViewmodel", "onDisconnected")
    }

    private fun onFail(message: String) {
        Log.d("AddRpiPageViewmodel", "onFail: $message")
    }
}