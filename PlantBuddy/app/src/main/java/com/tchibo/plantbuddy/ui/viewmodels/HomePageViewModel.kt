package com.tchibo.plantbuddy.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.controller.RaspberryInfoController
import com.tchibo.plantbuddy.domain.RaspberryInfoDto
import com.tchibo.plantbuddy.domain.ScreenInfo
import com.tchibo.plantbuddy.utils.Routes
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class HomePageState(
    val raspberryDtoList: List<RaspberryInfoDto> = listOf(),
    val screenInfo: ScreenInfo = ScreenInfo(),
    val isRefreshing: Boolean = false,
    val isRaspberryListLoading: Boolean = false,
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
                isRaspberryListLoading = true,
            )

            val raspberryDtoList = RaspberryInfoController.INSTANCE.getRaspberryInfoDtoList()

            val screenInfo = ScreenInfo(
                navigationIcon = Icons.Filled.Settings,
                onNavigationIconClick = {
                    navigator.navigate(Routes.getNavigateSettings())
                },
            )

            val localToken = Firebase.messaging.token.await()
            FirebaseController.INSTANCE.updateLocalToken(localToken)
            navigator.clearBackStack(Routes.getNavigateAdd())

            _state.value = _state.value.copy(
                screenInfo = screenInfo,
                raspberryDtoList = raspberryDtoList,
                isRaspberryListLoading = false,
            )
        }
    }

    fun reloadRaspberryDtoList() {
        println("Loading raspberry dto list...")

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = true,
            )

            val raspberryDtoList = RaspberryInfoController.INSTANCE.getRaspberryInfoDtoList()
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