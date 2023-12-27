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
import com.tchibo.plantbuddy.domain.MoistureInfo
import com.tchibo.plantbuddy.domain.MoistureInfoDto
import com.tchibo.plantbuddy.domain.ScreenInfo
import kotlinx.coroutines.flow.toList
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

            val raspberryInfo = LocalDbController.INSTANCE.getRaspberryInfo(raspberryId)
            println("Raspberry info: $raspberryInfo")

            val moistureInfoList: List<MoistureInfo?> =
                LocalDbController.INSTANCE.getMoistureInfoForRaspId(raspberryId).toList()

            val moistureInfoDtoList = moistureInfoList.stream()
                .filter { it != null }
                .map { MoistureInfoDto(it!!.measurementValuePercent, it.measurementTime) }
                .collect(toList())

            val chartModelProducer = ChartEntryModelProducer(
                moistureInfoDtoList.map { entryOf(it.measurementTime.nanos, it.measurementValuePercent.toFloat()) }
            )

            _state.value = _state.value.copy(
                screenInfo = screenInfo,
                deviceDetails = if (raspberryInfo != null)
                    DeviceDetails(raspberryInfo, moistureInfoDtoList)
                            else DeviceDetails(),
                isRefreshing = false,
                chartModelProducer = chartModelProducer,
            )
        }
    }
}