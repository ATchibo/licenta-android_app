package com.tchibo.plantbuddy.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.State
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
            println("Loaded raspberry info.")

            val moistureInfoList = async {
                MoistureInfoController.INSTANCE.getMoistureInfoForRaspId(raspberryId)
            }
            println("Loaded moisture info.")

            val moistureInfoDtoList = moistureInfoList.await().stream()
                .filter { it != null }
                .map { MoistureInfoDto(it!!.measurementValuePercent, it.measurementTime) }
                .collect(toList())
            println("Converted moisture info: $moistureInfoDtoList")

            val chartModelProducer = ChartEntryModelProducer(
                moistureInfoDtoList.map {
                    entryOf(moistureInfoDtoList.indexOf(it) + 1, it.measurementValuePercent)
                }
            )

            val moistureMaps = moistureInfoDtoList.map {
                moistureInfoDtoList.indexOf(it) + 1.0f to (it.measurementTime to it.measurementValuePercent)
            }.toMap()

            println("Loaded initial data.")

            _state.value = _state.value.copy(
                screenInfo = screenInfo,
                raspberryInfo = raspberryInfo.await()!!,
                moistureMaps = moistureMaps,
                isRefreshing = false,
                chartModelProducer = chartModelProducer,
            )
        }
    }

    fun goToWateringOptions() {
        navigator.navigate(
            Routes.getNavigateWateringOptions(raspberryId),
        )
    }
}