package com.tchibo.plantbuddy.ui.pages

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
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.domain.RaspberryStatus
import com.tchibo.plantbuddy.ui.components.Appbar
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
                text = state.raspberryInfo.raspberryName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                fontSize = TEXT_SIZE_BIG,
                fontWeight = FontWeight.Medium,
                color = Color.White,
            )

            Text(
                text = state.raspberryInfo.raspberryStatus.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp),
                fontSize = TEXT_SIZE_NORMAL,
                fontWeight = FontWeight.Medium,
                color = when (state.raspberryInfo.raspberryStatus) {
                    RaspberryStatus.ONLINE -> MaterialTheme.colorScheme.primary
                    RaspberryStatus.OFFLINE -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onBackground
                },
            )

            Chart(
                chart = lineChart(),
                chartModelProducer = state.chartModelProducer,
                startAxis = rememberStartAxis(
                    title = "Moisture percentage",
                ),
                bottomAxis = rememberBottomAxis(
                    title = "Time",
                ),
            )
        }
    }
}