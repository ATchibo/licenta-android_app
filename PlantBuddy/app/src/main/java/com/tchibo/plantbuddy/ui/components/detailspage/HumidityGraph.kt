package com.tchibo.plantbuddy.ui.components.detailspage;

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.decoration.rememberHorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
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
    val moistureValuesList = state.moistureValuesList

    if (moistureValuesList.isEmpty()) {
        val model = CartesianChartModel(LineCartesianLayerModel.build { series(0f) })

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    axisValueOverrider = AxisValueOverrider.fixed(minY = 0f, maxY = 100f),
                ),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(
                    title = "Time",
                    valueFormatter = { _, _, _ ->
                        return@rememberBottomAxis noDataString
                    },
                    labelRotationDegrees = -45f,
                ),
            ),
            model = model,
            modifier = Modifier,
        )

        return
    }

    val model = CartesianChartModel(LineCartesianLayerModel.build { series(moistureValuesList) })

    val marker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            padding = Dimensions.of(5.dp),
            margins = Dimensions.of(5.dp),
        ),
        labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint,
        indicator = rememberShapeComponent(Shape.Pill, MaterialTheme.colorScheme.onSurface),
    )

    val markersMap = mutableMapOf<Float, CartesianMarker>()
    for (i in moistureValuesList.indices) {
        markersMap[i.toFloat()] = marker
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                axisValueOverrider = AxisValueOverrider.fixed(minY = 0f, maxY = 100f),
                spacing = 100.dp,
            ),
            startAxis = rememberStartAxis(
                valueFormatter = { value, _, _->
                    val d = round(value * 100) / 100
                    "$d%"
                },
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = { value, _, _ ->
                    val formatter = DateTimeFormatter.ofPattern("MMM dd HH:mm")
                    val timestamp = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(state.moistureValuesTimestampsMap[value]?.seconds ?: 0),
                        ZoneId.systemDefault()
                    )
                    formatter.format(timestamp)
                },
                labelRotationDegrees = -45f,
                sizeConstraint = BaseAxis.SizeConstraint.Exact(100f),
            ),
            persistentMarkers = markersMap,
            decorations = listOf(
                rememberHorizontalLine(
                    y = { state.averageMoistureValue },
                    line = rememberLineComponent(color = MaterialTheme.colorScheme.primary, thickness = 2.dp),
                    labelComponent =
                    rememberTextComponent(
                        MaterialTheme.colorScheme.primary,
                        padding = Dimensions.of(horizontal = 8.dp),
                    ),
                    label = { "Average: ${state.averageMoistureValue}%" }
                ),
            )
        ),
        model = model,
        modifier = Modifier
            .height(250.dp),
        marker = marker,
        scrollState = rememberVicoScrollState(scrollEnabled = true),
    )
}
