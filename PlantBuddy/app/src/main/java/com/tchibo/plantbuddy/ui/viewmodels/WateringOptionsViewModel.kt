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
import com.tchibo.plantbuddy.domain.WateringProgram
import kotlinx.coroutines.launch


data class WateringOptionsState(
    val screenInfo: ScreenInfo = ScreenInfo(),
    val isRefreshing: Boolean = false,
    val isWatering: Boolean = false,
    val currentWateringVolume: String = "0",
    val currentWateringDuration: String = "0",

    val wateringPrograms: List<WateringProgram> = mutableListOf(),
    var currentWateringProgramOptionIndex: Int = -1,
    var isWateringProgramsEnabled: Boolean = true,
    var isWateringProgramInfoPopupOpen: Boolean = false,
    var previewWateringOptionIndex: Int = -1,
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

            val wateringPrograms = FirebaseController.INSTANCE.getWateringPrograms(raspberryId)

            _state.value = _state.value.copy(
                isRefreshing = false,
                screenInfo = screenInfo,
                currentWateringDuration = "0",
                currentWateringVolume = "0",
                wateringPrograms = wateringPrograms,
                currentWateringProgramOptionIndex = -1,
                isWateringProgramsEnabled = true,
                isWateringProgramInfoPopupOpen = false,
                previewWateringOptionIndex = -1,
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
            } else if (wateringInfo.getWateringCommand() == "start_watering") {
                startWatering();
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

    fun selectWateringOption(index: Int) {
        _state.value = _state.value.copy(
            previewWateringOptionIndex = -1,
            currentWateringProgramOptionIndex = index,
        )
    }

    fun toggleEnabledWateringPrograms() {
        _state.value = _state.value.copy(
            isWateringProgramsEnabled = !_state.value.isWateringProgramsEnabled,
        )
    }

    @Composable
    fun getCurrentWateringProgramName(): String {
        if (state.value.currentWateringProgramOptionIndex == -1) {
            return stringResource(id = R.string.no_watering_program)
        }
        return state.value.wateringPrograms[
            state.value.currentWateringProgramOptionIndex
        ].getName()
    }

    fun onWateringProgramTap(index: Int) {
        Log.d("INDEX", "onWateringProgramTap: $index")
        _state.value = _state.value.copy(
            isWateringProgramInfoPopupOpen = true,
            previewWateringOptionIndex = index,
        )
    }

    fun closeWateringProgramInfoPopup() {
        _state.value = _state.value.copy(
            isWateringProgramInfoPopupOpen = false,
            previewWateringOptionIndex = -1,
        )
    }

    fun getPreviewWateringProgramName(): String {
        if (state.value.previewWateringOptionIndex == -1) {
            return ""
        }
        return state.value.wateringPrograms[
            state.value.previewWateringOptionIndex
        ].getName()
    }

    fun getPreviewWateringProgramFrequencyDays(): Int {
        Log.d("INDEX", "getPreviewWateringProgramFrequencyDays: ${state.value.previewWateringOptionIndex}")
        Log.d("INDEX", "program: ${state.value.wateringPrograms[
            state.value.previewWateringOptionIndex
        ]}")

        if (state.value.previewWateringOptionIndex == -1) {
            return 0
        }
        return state.value.wateringPrograms[
            state.value.previewWateringOptionIndex
        ].getFrequencyDays()
    }

    fun getPreviewWateringProgramQuantityL(): Float {
        if (state.value.previewWateringOptionIndex == -1) {
            return 0.0f
        }
        return state.value.wateringPrograms[
            state.value.previewWateringOptionIndex
        ].getQuantityL()
    }

    fun getPreviewWateringProgramTimeOfDayMin(): Int {
        if (state.value.previewWateringOptionIndex == -1) {
            return 0
        }
        return state.value.wateringPrograms[
            state.value.previewWateringOptionIndex
        ].getTimeOfDayMin()
    }

    fun getPreviewWateringProgramTimeOfDay(): String {
        if (state.value.previewWateringOptionIndex == -1) {
            return ""
        }
        val timeOfDayMin = state.value.wateringPrograms[
            state.value.previewWateringOptionIndex
        ].getTimeOfDayMin()
        val hours = timeOfDayMin / 60
        val minutes = timeOfDayMin % 60
        return String.format("%02d:%02d", hours, minutes)
    }

    fun reloadWateringPrograms() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = true,
            )

            val wateringPrograms = FirebaseController.INSTANCE.getWateringPrograms(raspberryId)

            _state.value = _state.value.copy(
                isRefreshing = false,
                wateringPrograms = wateringPrograms,
            )
        }
    }
}