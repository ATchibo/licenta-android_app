package com.tchibo.plantbuddy.ui.components.detailspage;

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.ui.viewmodels.DetailsPageState
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.round

@Composable
fun HumidityGraph(
    state: DetailsPageState,
) {

    val noDataString = stringResource(id = R.string.no_data)

    Chart(
        modifier = Modifier
            .height(250.dp),
        chart = lineChart(
            axisValuesOverrider = AxisValuesOverrider.fixed(
                minY = 0f, maxY = 100f),
            spacing = 100.dp,
        ),
        chartModelProducer = state.chartModelProducer,
        startAxis = rememberStartAxis(
            title = "Moisture percentage",
            valueFormatter = { value, _ ->
                val d = round(value * 100) / 100 // just 2 decimals
                "$d%"
            },
        ),
        bottomAxis = rememberBottomAxis(
            title = "Time",
            valueFormatter = { value, _ ->
                if (value == 0f)
                    return@rememberBottomAxis noDataString

                val formatter = DateTimeFormatter.ofPattern("MMM dd HH:mm")
                val timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(state.moistureMaps[value]?.first?.seconds ?: 0),
                    ZoneId.systemDefault()
                )
                formatter.format(timestamp)
            },
            labelRotationDegrees = 45f,
            sizeConstraint = Axis.SizeConstraint.Exact(100f),
        ),
    )
}