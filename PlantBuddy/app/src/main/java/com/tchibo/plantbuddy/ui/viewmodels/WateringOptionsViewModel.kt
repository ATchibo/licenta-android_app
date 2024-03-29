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
import com.tchibo.plantbuddy.utils.Routes
import kotlinx.coroutines.launch


data class WateringOptionsState(
    val screenInfo: ScreenInfo = ScreenInfo(),
    val isRefreshing: Boolean = false,
    val isWatering: Boolean = false,
    val currentWateringVolume: String = "N/A",
    val currentMoistureLevel: String = "N/A",
    val currentWaterVolume: String = "N/A",
    val currentWateringDuration: String = "N/A",
    var isLoadingInitData: Boolean = false,

    val wateringPrograms: List<WateringProgram> = mutableListOf(),
    var currentWateringProgramOptionIndex: Int = -1,
    var isWateringProgramsEnabled: Boolean = true,
    var isWateringProgramInfoPopupOpen: Boolean = false,
    var previewWateringOptionIndex: Int = -1,

    val isProgramDeletePopupOpen: Boolean = false,
    val programDeletePopupTitle: String = "",
    val programDeletePopupMessage: String = "",
)

class WateringOptionsViewModel (
    private val navigator: NavHostController,
    private val raspberryId: String
): ViewModel() {

    private val _state = mutableStateOf(WateringOptionsState())
    val state: State<WateringOptionsState> = _state

    private var listenerRegistration: ListenerRegistration? = null

    private var programToDelete: String = ""

    init {
        initLoading()
    }

    private fun initLoading() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoadingInitData = true,
            )

            val screenInfo = ScreenInfo(
                navigationIcon = Icons.Filled.ArrowBack,
                onNavigationIconClick = {
                    navigator.popBackStack()
                },
            )

            reloadWateringPrograms()

            _state.value = _state.value.copy(
                isLoadingInitData = false,
                screenInfo = screenInfo,
                currentWateringDuration = "0",
                currentWateringVolume = "0",
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
        FirebaseController.INSTANCE.setActiveWateringProgramId(
            raspberryId,
            state.value.wateringPrograms[index].getId()
        )
        _state.value = _state.value.copy(
            currentWateringProgramOptionIndex = index,
        )
    }

    fun toggleEnabledWateringPrograms() {
        FirebaseController.INSTANCE.setIsWateringProgramsActive(
            raspberryId,
            !_state.value.isWateringProgramsEnabled
        )
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

    fun getPreviewWateringProgram() : WateringProgram? {
        if (state.value.previewWateringOptionIndex == -1) {
            return null
        }
        return state.value.wateringPrograms[
            state.value.previewWateringOptionIndex
        ]
    }

    fun reloadWateringPrograms() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = true,
            )

            val wateringPrograms = FirebaseController.INSTANCE.getWateringPrograms(raspberryId)
            val activeWateringProgramId = FirebaseController.INSTANCE.getActiveWateringProgramId(raspberryId)
            var activeWateringProgramIndex = -1
            for (program in wateringPrograms) {
                if (program.getId() == activeWateringProgramId) {
                    activeWateringProgramIndex = wateringPrograms.indexOf(program)
                    break
                }
            }
            val isWateringProgramsEnabled = FirebaseController.INSTANCE.getIsWateringProgramsActive(raspberryId)

            _state.value = _state.value.copy(
                isRefreshing = false,
                wateringPrograms = wateringPrograms,
                currentWateringProgramOptionIndex = activeWateringProgramIndex,
                isWateringProgramsEnabled = isWateringProgramsEnabled,
            )
        }
    }

    fun goToAddWateringProgram() {
        navigator.navigate(Routes.getNavigateAddProgram(raspberryId))
    }

    fun goToEditWateringProgram(programId: String) {
        Log.d("TAG", "Edit program: $programId")
        navigator.navigate(Routes.getNavigateAddProgram(raspberryId, programId))
    }

    fun onPressWateringProgramDelete(programId: String) {
        val program = state.value.wateringPrograms.find { it.getId() == programId }
        if (program != null) {
            _state.value = _state.value.copy(
                isProgramDeletePopupOpen = true,
                programDeletePopupTitle = "Are you sure you want to delete this?",
                programDeletePopupMessage = program.toStringBody(),
            )

            programToDelete = programId
        }
    }

    fun deleteWateringProgram() {
        if (programToDelete.isEmpty()) {
            return
        }

        FirebaseController.INSTANCE.deleteWateringProgram(raspberryId, programToDelete)
        closeProgramDeletePopup()
        reloadWateringPrograms()
    }

    fun closeProgramDeletePopup() {
        _state.value = _state.value.copy(
            isProgramDeletePopupOpen = false,
        )
    }

    fun checkMoisture() {
        _state.value = _state.value.copy(
            currentMoistureLevel = "Checking...",
        )

        viewModelScope.launch {
            val moistureLevel = FirebaseController.INSTANCE.getMoistureLevel(raspberryId)
            _state.value = _state.value.copy(
                currentMoistureLevel = "$moistureLevel%",
            )
        }
    }

    fun checkWaterVolume() {
        _state.value = _state.value.copy(
            currentWaterVolume = "Checking...",
        )

        viewModelScope.launch {
            val waterVolume = FirebaseController.INSTANCE.getWaterVolume(raspberryId)
            _state.value = _state.value.copy(
                currentWaterVolume = "$waterVolume liters",
            )
        }
    }
}