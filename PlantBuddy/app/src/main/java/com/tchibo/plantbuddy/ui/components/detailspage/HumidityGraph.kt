package com.tchibo.plantbuddy.ui.components.detailspage;

import androidx.compose.runtime.Composable
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.tchibo.plantbuddy.ui.viewmodels.DetailsPageState
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HumidityGraph(
    state: DetailsPageState,
) {

    Chart(
        chart = lineChart(
            axisValuesOverrider = AxisValuesOverrider.fixed(
                minY = 0f, maxY = 100f)
        ),
        chartModelProducer = state.chartModelProducer,
        startAxis = rememberStartAxis(
            title = "Moisture percentage",
            valueFormatter = { value, _ ->
                "$value%"
            },
        ),
        bottomAxis = rememberBottomAxis(
            title = "Time",
            valueFormatter = { value, _ ->
                val formatter = DateTimeFormatter.ofPattern("MMM dd HH:mm")
                val timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(state.moistureMaps[value]?.first?.seconds ?: 0),
                    ZoneId.systemDefault()
                )
                formatter.format(timestamp)
            },
        ),
    )
}