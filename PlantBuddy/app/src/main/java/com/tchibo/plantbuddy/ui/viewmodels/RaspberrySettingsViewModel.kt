package com.tchibo.plantbuddy.ui.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.domain.ScreenInfo
import kotlinx.coroutines.launch


data class RaspberrySettingsViewModelState (
    val loading: Boolean = false,
    val screenInfo: ScreenInfo = ScreenInfo(),

    val raspberryName: String = "",
    val raspberryDescription: String? = "",
    val raspberryLocation: String? = "",
    val notifiableMessages: HashMap<String, Boolean> = hashMapOf(),

    val editIcon: ImageVector = Icons.Default.Edit,
    val confirmIcon: ImageVector = Icons.Default.Check,
    val cancelIcon: ImageVector = Icons.Default.Cancel,

    val editingName: Boolean = false,
    val editingDescription: Boolean = false,
    val editingLocation: Boolean = false,
)

class RaspberrySettingsViewModel (
    private val navigator: NavHostController,
    private val raspberryId: String,
): ViewModel() {

    private val _state = mutableStateOf(RaspberrySettingsViewModelState())
    val state = _state

    init {
        loadRaspberryInfo()
    }

    fun loadRaspberryInfo() {
        _state.value = _state.value.copy(
            loading = true
        )

        val screenInfo = ScreenInfo(
            navigationIcon = Icons.Default.ArrowBack,
            onNavigationIconClick = {
                navigator.popBackStack()
            },
        )

        _state.value = _state.value.copy(
            screenInfo = screenInfo
        )

        viewModelScope.launch {
            val raspberryInfo = FirebaseController.INSTANCE.getRaspberryInfo(raspberryId)

            if (raspberryInfo == null) {
                Toast.makeText(
                    navigator.context,
                    "Raspberry not found",
                    Toast.LENGTH_SHORT
                ).show()

                navigator.popBackStack()
                return@launch
            }

            _state.value = _state.value.copy(
                loading = false,
                raspberryName = raspberryInfo.raspberryName,
                raspberryDescription = raspberryInfo.raspberryDescription,
                raspberryLocation = raspberryInfo.raspberryLocation,
                notifiableMessages = raspberryInfo.notifiableMessages
            )
        }
    }

    fun onNameEditClick() {
        _state.value = _state.value.copy(
            editingName = true,
        )
    }

    fun onNameCancelEditClick() {
        _state.value = _state.value.copy(
            editingName = false,
        )
    }

    fun onNameConfirmEditClick(value: String) {
        FirebaseController.INSTANCE.setRaspberryName(
            raspberryId,
            value,
            {
                Toast.makeText(
                    navigator.context,
                    "Raspberry name updated",
                    Toast.LENGTH_SHORT
                ).show()

                _state.value = _state.value.copy(
                    editingName = false,
                    raspberryName = value,
                )
            },
            { error ->
                Toast.makeText(
                    navigator.context,
                    "Error updating raspberry name: $error",
                    Toast.LENGTH_SHORT
                ).show()

                _state.value = _state.value.copy(
                    editingName = false,
                )
            }
        )
    }

    fun onDescriptionEditClick() {
        _state.value = _state.value.copy(
            editingDescription = true,
        )
    }

    fun onDescriptionCancelEditClick() {
        _state.value = _state.value.copy(
            editingDescription = false,
        )
    }

    fun onDescriptionConfirmEditClick(value: String) {
        FirebaseController.INSTANCE.setRaspberryDescription(
            raspberryId,
            value,
            {
                Toast.makeText(
                    navigator.context,
                    "Raspberry description updated",
                    Toast.LENGTH_SHORT
                ).show()

                _state.value = _state.value.copy(
                    editingDescription = false,
                    raspberryDescription = value,
                )
            },
            { error ->
                Toast.makeText(
                    navigator.context,
                    "Error updating raspberry description: $error",
                    Toast.LENGTH_SHORT
                ).show()

                _state.value = _state.value.copy(
                    editingDescription = false,
                )
            }
        )
    }

    fun onLocationEditClick() {
        _state.value = _state.value.copy(
            editingLocation = true,
        )
    }

    fun onLocationCancelEditClick() {
        _state.value = _state.value.copy(
            editingLocation = false,
        )
    }

    fun onLocationConfirmEditClick(value: String) {
        FirebaseController.INSTANCE.setRaspberryLocation(
            raspberryId,
            value,
            {
                Toast.makeText(
                    navigator.context,
                    "Raspberry location updated",
                    Toast.LENGTH_SHORT
                ).show()

                _state.value = _state.value.copy(
                    editingLocation = false,
                    raspberryLocation = value,
                )
            },
            { error ->
                Toast.makeText(
                    navigator.context,
                    "Error updating raspberry location: $error",
                    Toast.LENGTH_SHORT
                ).show()

                _state.value = _state.value.copy(
                    editingLocation = false,
                )
            }
        )
    }

    fun onNotifiableMessageValueChange(key: String, value: Boolean, onSuccess: (Boolean) -> Unit) {
        Log.d("RaspberrySettingsViewModel", "onNotifiableMessageValueChange: $key, $value")

        FirebaseController.INSTANCE.setNotifiableMessage(
            raspberryId,
            key,
            value,
            {
                _state.value = _state.value.copy(
                    notifiableMessages = _state.value.notifiableMessages.apply {
                        this[key] = value
                    }
                )

                onSuccess(value)
            },
            { error ->
                Toast.makeText(
                    navigator.context,
                    "Error updating notifiable message: $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    fun navigateBack() {
        navigator.popBackStack()
    }
}

class RaspberrySettingsViewModelFactory (
    private val navigator: NavHostController,
    private val raspberryId: String
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(RaspberrySettingsViewModel::class.java)) {
            return RaspberrySettingsViewModel(navigator, raspberryId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
