package com.tchibo.plantbuddy.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.tchibo.plantbuddy.controller.MoistureInfoController
import com.tchibo.plantbuddy.controller.RaspberryInfoController
import com.tchibo.plantbuddy.domain.MoistureInfoDto
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.ScreenInfo
import com.tchibo.plantbuddy.utils.Routes
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.stream.Collectors.toList
import kotlin.random.Random

data class DetailsPageState(
    val screenInfo: ScreenInfo = ScreenInfo(),
    val raspberryInfo: RaspberryInfo = RaspberryInfo(),
    val moistureMaps: Map<Float, Pair<Timestamp, Float>> = mutableMapOf(),
    val isRefreshing: Boolean = false,
    val isHumidityDropdownExpanded: MutableState<Boolean> = mutableStateOf(false),
    val humidityDropdownOptions: MutableList<String> = mutableListOf("Last 24h", "Last 7 days", "Last 30 days"),
    val currentHumidityDropdownOptionIndex: MutableState<Int> = mutableIntStateOf(0),
    val chartModelProducer: ChartEntryModelProducer =
        ChartEntryModelProducer(List(4) { entryOf(it, Random.nextFloat() * 16f) })
)

class DetailsPageViewmodel(
    private val navigator: NavHostController,
    private val raspberryId: String,
): ViewModel() {

    private val _state = mutableStateOf(DetailsPageState())
    val state: State<DetailsPageState> = _state

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

            val raspberryInfo = async {
                RaspberryInfoController.INSTANCE.getRaspberryInfo(raspberryId)
            }

            updateHumidityValuesList()

            _state.value = _state.value.copy(
                screenInfo = screenInfo,
                raspberryInfo = raspberryInfo.await()!!,
                isRefreshing = false,
            )
        }
    }

    fun goToWateringOptions() {
        navigator.navigate(
            Routes.getNavigateWateringOptions(raspberryId),
        )
    }

    fun onHumidityDropdownOptionSelected(index: Int) {
        _state.value = _state.value.copy(
            currentHumidityDropdownOptionIndex = mutableIntStateOf(index),
        )

        updateHumidityValuesList()

        closeHumidityDropdown()
    }

    fun closeHumidityDropdown() {
        _state.value = _state.value.copy(
            isHumidityDropdownExpanded = mutableStateOf(false),
        )
    }

//    fun openHumidityDropdown() {
//        _state.value = _state.value.copy(
//            isHumidityDropdownExpanded = mutableStateOf(true),
//        )
//    }

    fun toggleHumidityDropdown() {
        _state.value = _state.value.copy(
            isHumidityDropdownExpanded = mutableStateOf(!_state.value.isHumidityDropdownExpanded.value),
        )
    }

    fun getCurrentHumidityDropdownOption(): String {
        return _state.value.humidityDropdownOptions[
            _state.value.currentHumidityDropdownOptionIndex.value
        ]
    }

    private fun updateHumidityValuesList() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = true,
            )

            val endTimestamp: Timestamp = Timestamp.now()
            val startTimestamp: Timestamp = when (_state.value.currentHumidityDropdownOptionIndex.value) {
                0 -> Timestamp(endTimestamp.seconds - 24 * 60 * 60, endTimestamp.nanoseconds)
                1 -> Timestamp(endTimestamp.seconds - 7 * 24 * 60 * 60, endTimestamp.nanoseconds)
                2 -> Timestamp(endTimestamp.seconds - 30 * 24 * 60 * 60, endTimestamp.nanoseconds)
                else -> Timestamp(endTimestamp.seconds - 24 * 60 * 60, endTimestamp.nanoseconds)
            }

            val moistureInfoList = async {
                MoistureInfoController.INSTANCE.getMoistureInfoForRaspId(
                    raspberryId,
                    startTimestamp,
                    endTimestamp,
                )
            }

            val moistureInfoDtoList = moistureInfoList.await().stream()
                .filter { it != null }
                .map { MoistureInfoDto(it!!.measurementValuePercent, it.measurementTime) }
                .collect(toList())

            val chartModelProducer = ChartEntryModelProducer(
                moistureInfoDtoList.map {
                    entryOf(moistureInfoDtoList.indexOf(it) + 1, it.measurementValuePercent)
                }
            )

            val moistureMaps = moistureInfoDtoList.associate {
                moistureInfoDtoList.indexOf(it) + 1.0f to (it.measurementTime to it.measurementValuePercent)
            }

            _state.value = _state.value.copy(
                moistureMaps = moistureMaps,
                chartModelProducer = chartModelProducer,
                isRefreshing = false,
            )
        }
    }
}