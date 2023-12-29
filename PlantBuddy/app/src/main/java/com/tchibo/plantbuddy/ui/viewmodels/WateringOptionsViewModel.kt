package com.tchibo.plantbuddy.ui.viewmodels

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.domain.ScreenInfo
import kotlinx.coroutines.launch


data class WateringOptionsState(
    val screenInfo: ScreenInfo = ScreenInfo(),
    val isRefreshing: Boolean = false,
)

class WateringOptionsViewModel (
    private val navigator: NavHostController,
    private val raspberryId: String
): ViewModel() {

    private val _state = mutableStateOf(WateringOptionsState())
    val state: State<WateringOptionsState> = _state

    var listenerRegistration: ListenerRegistration? = null

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
                screenInfo = screenInfo
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
        } else {
            Log.d("TAG", "Current data: null")
        }
    }

    fun startWatering() {
        FirebaseController.INSTANCE.startWatering(raspberryId)
    }

    fun stopWatering() {
        FirebaseController.INSTANCE.stopWatering(raspberryId)
    }
}