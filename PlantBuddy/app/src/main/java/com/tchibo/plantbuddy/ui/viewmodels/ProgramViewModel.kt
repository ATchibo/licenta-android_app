package com.tchibo.plantbuddy.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.tchibo.plantbuddy.domain.ScreenInfo
import com.tchibo.plantbuddy.domain.WateringProgram
import java.util.Calendar


data class ProgramState (
    val screenInfo: ScreenInfo = ScreenInfo(),
    val isRefreshing: Boolean = false,
    val id: String = "",
    val name: String = "",
    val frequencyDays: String = "",
    val quantityL: String = "",
    val timeOfDayMin: String = "",
    val minMoisture: String = "",
    val maxMoisture: String = "",
)

class ProgramViewModel (
    private val navigator: NavHostController,
    private val raspberryId: String,
): ViewModel() {

    private val _state = mutableStateOf(ProgramState())
    val state = _state

    init {
        initLoading()
    }

    private fun initLoading() {
        _state.value = _state.value.copy(
            isRefreshing = true,
        )

        val screenInfo = ScreenInfo(
            navigationIcon = Icons.Filled.ArrowBack,
            onNavigationIconClick = {
                navigator.popBackStack()
            },
        )

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)

        _state.value = _state.value.copy(
            screenInfo = screenInfo,
            isRefreshing = false,
            timeOfDayMin = (currentHour * 60 + currentMinute).toString()
        )
    }

    fun setInitialValues(wateringProgram: WateringProgram) {
        _state.value = _state.value.copy(
            id = wateringProgram.getId(),
            name = wateringProgram.getName(),
            frequencyDays = wateringProgram.getFrequencyDays().toString(),
            quantityL = wateringProgram.getQuantityL().toString(),
            timeOfDayMin = wateringProgram.getTimeOfDayMin().toString(),
            minMoisture = wateringProgram.getMinMoisture().toString(),
            maxMoisture = wateringProgram.getMaxMoisture().toString(),
        )
    }

    fun onNameChanged(name: String) {
        _state.value = _state.value.copy(
            name = name
        )
    }

    fun onFrequencyDaysChanged(frequencyDays: String) {
        _state.value = _state.value.copy(
            frequencyDays = frequencyDays
        )
    }

    fun onQuantityLChanged(quantityL: String) {
        _state.value = _state.value.copy(
            quantityL = quantityL
        )
    }

    fun onTimeOfDayChanged(hour: Int, minute: Int) {
        _state.value = _state.value.copy(
            timeOfDayMin = (hour * 60 + minute).toString()
        )
    }

    fun onMinMoistureChanged(minMoisture: String) {
        _state.value = _state.value.copy(
            minMoisture = minMoisture
        )
    }

    fun onMaxMoistureChanged(maxMoisture: String) {
        _state.value = _state.value.copy(
            maxMoisture = maxMoisture
        )
    }

    fun isNameValid(): Boolean {
        return _state.value.name.isNotEmpty()
    }

    fun isFrequencyDaysValid(): Boolean {
        return try {
            val frequencyDays: Float = _state.value.frequencyDays.toFloat()
            frequencyDays > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isQuantityLValid(): Boolean {
        return try {
            val quantityL: Float = _state.value.quantityL.toFloat()
            quantityL > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isTimeOfDayValid(): Boolean {
        return try {
            val timeOfDayMin: Int = _state.value.timeOfDayMin.toInt()
            timeOfDayMin in 0..1439
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isMinMoistureValid(): Boolean {
        return try {
            val minMoisture: Float = _state.value.minMoisture.toFloat()
            minMoisture in 0.1f..99.9f
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isMaxMoistureValid(): Boolean {
        return try {
            val maxMoisture: Float = _state.value.maxMoisture.toFloat()
            maxMoisture in 0.1f..99.9f
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isFormValid(): Boolean {
        return isNameValid() && isFrequencyDaysValid() && isQuantityLValid() && isTimeOfDayValid() && isMinMoistureValid() && isMaxMoistureValid()
    }

    fun getTimeOfDayString(): String {
        if (!isTimeOfDayValid())
            return "Invalid time of day"

        val hour = _state.value.timeOfDayMin.toInt() / 60
        val minute = _state.value.timeOfDayMin.toInt() % 60
        return String.format("%02d:%02d", hour, minute)
    }

    fun getTimeOfDayHour(): Int {
        if (!isTimeOfDayValid())
            return -1
        return _state.value.timeOfDayMin.toInt() / 60
    }

    fun getTimeOfDayMinute(): Int {
        if (!isTimeOfDayValid())
            return -1
        return _state.value.timeOfDayMin.toInt() % 60
    }

    fun onCancelButtonClicked() {
        navigator.popBackStack()
    }

    fun onSaveButtonClicked() {
        // TODO: save program
        navigator.popBackStack()
    }
}