package com.tchibo.plantbuddy.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

data class LoginAlertState(
    val showNotification: Boolean = false,
    val notificationMessage: String = "",
    val notificationTitle: String = "",
    val onOk: () -> Unit = {},
    val onCancel: () -> Unit = {},
)

@HiltViewModel
class LoginAlertViewModel @Inject constructor (

): ViewModel() {

    private val _state = MutableStateFlow(LoginAlertState())
    val state = _state

    init {
        initLoading()
    }

    private fun initLoading() {
        _state.value = _state.value.copy(
            showNotification = false
        )
    }

    fun showNotification(
        title: String,
        message: String,
        onOk: () -> Unit,
        onCancel: () -> Unit
    ) {
        _state.value = _state.value.copy(
            showNotification = true,
            notificationTitle = title,
            notificationMessage = message,
            onOk = onOk,
            onCancel = onCancel
        )
    }

    fun onOk() {
        _state.value.onOk()
        initLoading()
    }

    fun onCancel() {
        _state.value.onCancel()
        initLoading()
    }
}