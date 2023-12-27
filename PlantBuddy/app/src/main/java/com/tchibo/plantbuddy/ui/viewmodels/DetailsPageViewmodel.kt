package com.tchibo.plantbuddy.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.tchibo.plantbuddy.controller.db.LocalDbController
import com.tchibo.plantbuddy.domain.DeviceDetails
import com.tchibo.plantbuddy.domain.MoistureInfoDto
import com.tchibo.plantbuddy.domain.ScreenInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.stream.Collectors.toList
import kotlin.random.Random

data class DetailsPageState(
    val screenInfo: ScreenInfo = ScreenInfo(),
    val deviceDetails: DeviceDetails = DeviceDetails(),
    val isRefreshing: Boolean = false,

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
                LocalDbController.INSTANCE.getRaspberryInfo(raspberryId)
            }
            println("Loaded raspberry info.")

            val moistureInfoList = async {
                LocalDbController.INSTANCE.getMoistureInfoForRaspId(raspberryId)
//                listOf(
//                    MoistureInfo("0000000000000000", 1.0f, LocalDateTime.of(2021, 1, 1, 1, 1)),
//                    MoistureInfo("0000000000000000", 3.2f, LocalDateTime.of(2022, 1, 1, 1, 2)),
//                    MoistureInfo("0000000000000000", 2.1f, LocalDateTime.of(2023, 1, 1, 1, 3)),
//                )
            }
            println("Loaded moisture info.")

            val moistureInfoDtoList = moistureInfoList.await().stream()
                .filter { it != null }
                .map { MoistureInfoDto(it!!.measurementValuePercent, it.measurementTime) }
                .collect(toList())
            println("Converted moisture info.")

            val chartModelProducer = ChartEntryModelProducer(
                moistureInfoDtoList.map { entryOf(it.measurementTime.seconds / 1000000, it.measurementValuePercent) }
            )

            val deviceDetails = DeviceDetails(
                raspberryInfo = raspberryInfo.await()!!,
                moistureReadings = moistureInfoDtoList,
            )

            println("Loaded initial data.")

            _state.value = _state.value.copy(
                screenInfo = screenInfo,
                deviceDetails = deviceDetails,
                isRefreshing = false,
                chartModelProducer = chartModelProducer,
            )
        }
    }
}