package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.ui.viewmodels.WateringOptionsViewModel

@Composable
fun WateringOptionsPage (
    raspberryPiId: String
) {

    val navigator = LocalNavController.current
    val viewModel = viewModel<WateringOptionsViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WateringOptionsViewModel(navigator, raspberryPiId) as T
            }
        }
    )
    
    DisposableEffect(viewModel) {
        viewModel.addListener()
        onDispose {
            viewModel.removeListener()
        }
    }

    val state = viewModel.state.value

    Scaffold (
        topBar = { Appbar(screenInfo = state.screenInfo) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {

            Text(text = "WateringOptionsPage")

            Column {
                Button(onClick = { viewModel.startWatering() }) {
                    Text(text = "Water now")
                }

                Button(onClick = { viewModel.stopWatering() }) {
                    Text(text = "Stop watering")
                }
            }
        }
    }
}