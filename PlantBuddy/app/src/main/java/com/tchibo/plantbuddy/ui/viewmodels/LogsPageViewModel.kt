package com.tchibo.plantbuddy.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.domain.ScreenInfo
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class LogsPageViewModelState (
    val screenInfo: ScreenInfo = ScreenInfo(),
    val raspberryId: String,
    val logs: List<Pair<LocalDateTime, String>>,
    val isRefreshing: Boolean = false,
)

class LogsPageViewModel (
    private val navigator: NavHostController,
    private val raspberryId: String,
): ViewModel() {

    private val _state = mutableStateOf(
        LogsPageViewModelState(raspberryId = "raspberryId", logs = listOf())
    )
    val state = _state

    init {
        loadLogs()
    }

    fun loadLogs() {
        _state.value = _state.value.copy(
            raspberryId = raspberryId,
            isRefreshing = true,
        )

        val screenInfo = ScreenInfo(
            navigationIcon = Icons.Filled.ArrowBack,
            onNavigationIconClick = {
                navigator.popBackStack()
            },
        )

        viewModelScope.launch {
            val logs = getLogs()

            state.value = _state.value.copy(
                screenInfo = screenInfo,
                isRefreshing = false,
                logs = logs,
            )
        }
    }

    private suspend fun getLogs(): List<Pair<LocalDateTime, String>> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX")

        val logs: HashMap<String, Any> = FirebaseController.INSTANCE.getLogs(raspberryId)
        val logsList = mutableListOf<Pair<LocalDateTime, String>>()
        logs.forEach { (key, value) ->
            val dateTime = LocalDateTime.parse(key, formatter)
            logsList.add(Pair(dateTime, value as String))
        }

        return logsList.sortedByDescending { it.first }
    }

    fun navigateBack() {
        navigator.popBackStack()
    }
}