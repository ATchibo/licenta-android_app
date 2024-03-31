package com.tchibo.plantbuddy.ui.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.domain.ScreenInfo
import com.tchibo.plantbuddy.domain.WateringProgram
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date


data class ProgramState (
    val screenInfo: ScreenInfo = ScreenInfo(),
    val isRefreshing: Boolean = false,
    val id: String = "",
    val name: String = "",
    val frequencyDays: String = "",
    val quantityL: String = "",
    val timeOfDayMin: String = "",
    val selectedDate: String = "",
    val minMoisture: String = "",
    val maxMoisture: String = "",
)

class ProgramViewModel(
    private val navigator: NavHostController,
    private val raspberryId: String,
    private val programId: String,
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

        if (programId.isNotEmpty() && programId != "NULL") {
            try {
                FirebaseController.INSTANCE.getWateringProgram(
                    raspberryId,
                    programId,
                    onSuccess = { wateringProgram ->
                        setInitialValues(wateringProgram)
                    },
                    onFailure = {
                        Toast.makeText(
                            navigator.context,
                            "Failed to load watering program",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(
                    navigator.context,
                    "Failed to load watering program",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)

            _state.value = _state.value.copy(
                screenInfo = screenInfo,
                isRefreshing = false,
                timeOfDayMin = (currentHour * 60 + currentMinute).toString()
            )
        }
    }

    private fun setInitialValues(wateringProgram: WateringProgram?) {
        Log.d("TAG", "setInitialValues: $wateringProgram")
        if (wateringProgram == null) {
            return
        }

        val startingDateTime = LocalDateTime.ofInstant(wateringProgram.getStartingDateTime().toDate().toInstant(), ZoneId.systemDefault())
        val selectedDate = "${startingDateTime.dayOfMonth}.${startingDateTime.monthValue}.${startingDateTime.year}"
        val selectedTime = startingDateTime.hour*60 + startingDateTime.minute

        _state.value = _state.value.copy(
            id = wateringProgram.getId(),
            name = wateringProgram.getName(),
            frequencyDays = wateringProgram.getFrequencyDays().toString(),
            quantityL = wateringProgram.getQuantityL().toString(),
            timeOfDayMin = selectedTime.toString(),
            selectedDate = selectedDate,
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

    fun onDateChanged(selectedDate: Calendar) {
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)
        val month = selectedDate.get(Calendar.MONTH) + 1
        val year = selectedDate.get(Calendar.YEAR)
        _state.value = _state.value.copy(
            selectedDate = "$day.$month.$year"
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

    private fun isTimeOfDayValid(): Boolean {
        return try {
            val timeOfDayMin: Int = _state.value.timeOfDayMin.toInt()
            timeOfDayMin in 0..1439
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isDateValid(selectedDate: Calendar): Boolean {
        val now = Calendar.getInstance()
        now.add(Calendar.DATE, -1)
        return selectedDate.after(now)
    }

    fun isMinMoistureValid(): Boolean {
        return try {
            val minMoisture: Float = _state.value.minMoisture.toFloat()
            minMoisture in 0.0f..100.0f
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isMaxMoistureValid(): Boolean {
        return try {
            val maxMoisture: Float = _state.value.maxMoisture.toFloat()
            maxMoisture in 0.0f..100.0f && maxMoisture >= _state.value.minMoisture.toFloat()
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

    fun getSelectedDate(): String {
        return _state.value.selectedDate.ifEmpty {
            "Select date"
        }
    }

    fun onCancelButtonClicked() {
        navigator.popBackStack()
    }

    fun onSaveButtonClicked() {
        val dateFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val date = LocalDate.parse(_state.value.selectedDate, dateFormatter)
        val time = LocalTime.parse(getTimeOfDayString(), timeFormatter)

        // Combine date and time into a LocalDateTime
        val dateTime = LocalDateTime.of(date, time)
        val timestamp = Timestamp(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()))

        FirebaseController.INSTANCE.addWateringProgram(
            raspberryId,
            WateringProgram(
                id = _state.value.id,
                name = _state.value.name,
                frequencyDays = _state.value.frequencyDays.toFloat(),
                quantityL = _state.value.quantityL.toFloat(),
                startingDateTime = timestamp,
                minMoisture = _state.value.minMoisture.toFloat(),
                maxMoisture = _state.value.maxMoisture.toFloat(),
            ),
            onSuccess = {
                Toast.makeText(
                    navigator.context,
                    "Watering program saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
                navigator.popBackStack()
            },
            onFailure = {
                Toast.makeText(
                    navigator.context,
                    "Failed to save watering program",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}