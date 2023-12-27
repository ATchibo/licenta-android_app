package com.tchibo.plantbuddy.ui.components.detailspage;

import androidx.compose.runtime.Composable;
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.tchibo.plantbuddy.ui.viewmodels.DetailsPageState

@Composable
fun HumidityGraph(
    state: DetailsPageState,
) {

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