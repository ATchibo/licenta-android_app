package com.tchibo.plantbuddy.ui.viewmodels

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.domain.ScreenInfo
import com.tchibo.plantbuddy.domain.WateringInfo
import kotlinx.coroutines.launch


data class WateringOptionsState(
    val screenInfo: ScreenInfo = ScreenInfo(),
    val isRefreshing: Boolean = false,
    val isWatering: Boolean = false,
    val currentWateringVolume: String = "0",
    val currentWateringDuration: String = "0",
)

class WateringOptionsViewModel (
    private val navigator: NavHostController,
    private val raspberryId: String
): ViewModel() {

    private val _state = mutableStateOf(WateringOptionsState())
    val state: State<WateringOptionsState> = _state

    private var listenerRegistration: ListenerRegistration? = null

    init {
        initLoading()
    }

    private fun initLoading() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = true,
            )

            val screenInfo = ScreenInfo(
                navigationIcon = Icons.Filled.ArrowBack,
                onNavigationIconClick = {
                    navigator.popBackStack()
                },
            )

            _state.value = _state.value.copy(
                isRefreshing = false,
                screenInfo = screenInfo,
                currentWateringDuration = "0",
                currentWateringVolume = "0",
            )
        }
    }

    fun addListener() {
        listenerRegistration = FirebaseController.INSTANCE.createListenerForWateringNow(
            raspberryId,
            ::wateringNowListener
        )
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }

    private fun wateringNowListener(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w("TAG", "Listen failed.", e)
            return
        }

        if (snapshot != null && snapshot.exists()) {
            // Handle the updated data
            Log.d("TAG", "Current data: ${snapshot.data}")
            // Update your UI or perform necessary actions

            val wateringInfo = WateringInfo().fromMap(snapshot.data!!)

            _state.value = _state.value.copy(
                currentWateringDuration = wateringInfo.getWateringDuration(),
                currentWateringVolume = wateringInfo.getWateringVolume(),
            )

            if (wateringInfo.getWateringCommand() == "stop_watering") {
                stopWatering();
            }
        } else {
            Log.d("TAG", "Current data: null")
        }
    }

    private fun startWatering() {
        FirebaseController.INSTANCE.startWatering(raspberryId)
        _state.value = _state.value.copy(
            isWatering = true
        )
    }

    private fun stopWatering() {
        FirebaseController.INSTANCE.stopWatering(raspberryId)
        _state.value = _state.value.copy(
            isWatering = false
        )
    }

    fun toggleWatering() {
        if (state.value.isWatering) {
            stopWatering()
        } else {
            startWatering()
        }
    }

    @Composable
    fun getWateringButtonText(): String {
        return if (state.value.isWatering) {
            stringResource(id = R.string.stop_watering)
        } else {
            stringResource(id = R.string.start_watering)
        }
    }

    @Composable
    fun getWateringButtonColor(): Color {
        return if (state.value.isWatering) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        }
    }

    @Composable
    fun getWateringButtonTextColor(): Color {
        return if (state.value.isWatering) {
            MaterialTheme.colorScheme.onError
        } else {
            MaterialTheme.colorScheme.onPrimary
        }
    }

    fun getWateringButtonIcon(): ImageVector {
        return if (state.value.isWatering) {
            Icons.Filled.Stop
        } else {
            Icons.Filled.PlayArrow
        }
    }
}