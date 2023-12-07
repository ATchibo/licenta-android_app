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
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.ScreenInfo
import kotlinx.coroutines.launch
import kotlin.random.Random

data class DetailsPageState(
    val screenInfo: ScreenInfo = ScreenInfo(),
    val raspberryInfo: RaspberryInfo = RaspberryInfo(),
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

            _state.value = _state.value.copy(
                screenInfo = screenInfo,
                raspberryInfo = raspberryInfo ?: RaspberryInfo(),
                isRefreshing = false,
            )
        }
    }
}