package com.tchibo.plantbuddy.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.tchibo.plantbuddy.controller.db.LocalDbController
import com.tchibo.plantbuddy.domain.RaspberryInfoDto
import com.tchibo.plantbuddy.domain.ScreenInfo
import com.tchibo.plantbuddy.utils.Routes
import kotlinx.coroutines.launch

data class HomePageState(
    val raspberryDtoList: List<RaspberryInfoDto> = listOf(),
    val screenInfo: ScreenInfo = ScreenInfo(),
    val isRefreshing: Boolean = false,
)

class HomePageViewModel(
    private val navigator: NavHostController,
): ViewModel() {

    private val _state = mutableStateOf(HomePageState())
    val state: State<HomePageState> = _state

    init {
        initLoading()
    }

    private fun initLoading() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = true,
            )

            val raspberryDtoList = LocalDbController.INSTANCE.getRaspberryInfoDtoList()
            println("Raspberry dto list: $raspberryDtoList")

            val screenInfo = ScreenInfo(
                navigationIcon = Icons.Filled.Settings,
                onNavigationIconClick = {
                    navigator.navigate(Routes.getNavigateSettings())
                },
            )

            _state.value = _state.value.copy(
                screenInfo = screenInfo,
                raspberryDtoList = raspberryDtoList,
                isRefreshing = false,
            )
        }
    }

    fun reloadRaspberryDtoList() {
        println("Loading raspberry dto list...")

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = true,
            )

            val raspberryDtoList = LocalDbController.INSTANCE.refreshRaspberryInfoDtoList()
            println("Raspberry dto list: $raspberryDtoList")

            _state.value = _state.value.copy(
                raspberryDtoList = raspberryDtoList,
                isRefreshing = false,
            )
        }
    }

    fun onAddClick() {
        navigator.navigate(Routes.getNavigateAdd())
    }

}