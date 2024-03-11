package com.tchibo.plantbuddy.ui.components

import android.content.Intent
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.tchibo.plantbuddy.ui.viewmodels.LoginAlertViewModel

@Composable
fun LoginRequestAlertComponent (
    intent: Intent
) {

    val viewModel = hiltViewModel<LoginAlertViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        if (intent.hasExtra("title") && intent.hasExtra("body")) {
            viewModel.showNotification(
                title = intent.getStringExtra("title")!!,
                message = intent.getStringExtra("body")!!,
                // intent.getStringExtra("data")!!
                onOk = {
                    Log.d("LoginRequestAlertComponent", "onOk")
                },
                onCancel = {
                    Log.d("LoginRequestAlertComponent", "onCancel")
                }
            )
        }
    }

    LaunchedEffect(key1 = state.showNotification) {
        Log.d("LoginRequestAlertComponent", "showNotification: ${state.showNotification}")
    }

    if (state.showNotification) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog
                viewModel.onCancel()
            },
            title = {
                Text(text = state.notificationTitle)
            },
            text = {
                Text(text = state.notificationMessage)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onOk()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        viewModel.onCancel()
                    }
                ) {
                    Text("NO")
                }
            }
        )
    }
}