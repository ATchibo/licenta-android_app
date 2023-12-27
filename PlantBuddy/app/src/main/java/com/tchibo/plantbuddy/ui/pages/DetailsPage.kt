package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.domain.RaspberryStatus
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.ui.components.detailspage.HumidityGraph
import com.tchibo.plantbuddy.ui.viewmodels.DetailsPageViewmodel
import com.tchibo.plantbuddy.utils.TEXT_SIZE_BIG
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL

@Composable
fun DetailsPage(rpiId: String) {

    val navigator = LocalNavController.current
    val viewModel = viewModel<DetailsPageViewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DetailsPageViewmodel(navigator, rpiId) as T
            }
        }
    )
    val state = viewModel.state.value
    
    Scaffold (
        topBar = { Appbar(screenInfo = state.screenInfo) }
    ) { paddingValues ->  
        Column (
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            Text(
                text = state.deviceDetails.raspberryInfo.raspberryName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 10.dp, 0.dp, 0.dp),
                fontSize = TEXT_SIZE_BIG,
                fontWeight = FontWeight.Medium,
                color = Color.White,
            )

            Text(
                text = state.deviceDetails.raspberryInfo.raspberryStatus.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 10.dp, 0.dp, 20.dp),
                fontSize = TEXT_SIZE_NORMAL,
                fontWeight = FontWeight.Medium,
                color = when (state.deviceDetails.raspberryInfo.raspberryStatus) {
                    RaspberryStatus.ONLINE -> MaterialTheme.colorScheme.primary
                    RaspberryStatus.OFFLINE -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onBackground
                },
            )

            Box(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                HumidityGraph(
                    state = state
                )
            }
        }
    }
}