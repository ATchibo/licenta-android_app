package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.ui.components.wateringpage.WateringProgramLine
import com.tchibo.plantbuddy.ui.viewmodels.WateringOptionsViewModel
import com.tchibo.plantbuddy.utils.TEXT_SIZE_BIG
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_SMALL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_UGE

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

            if (state.isRefreshing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp, 0.dp, 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(32.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            } else {
                Text(
                    text = stringResource(id = R.string.watering_options),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 10.dp, 30.dp, 0.dp),
                    fontSize = TEXT_SIZE_UGE,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )

                Text(
                    text = stringResource(id = R.string.watering_options_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 10.dp, 30.dp, 40.dp),
                    fontSize = TEXT_SIZE_SMALL,
                    color = Color.White,
                )

                Spacer(modifier = Modifier.weight(1f))

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 10.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.manual_watering),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 0.dp, 20.dp),
                        fontSize = TEXT_SIZE_BIG,
                        fontWeight = FontWeight.Medium,
                    )

                    Button(
                        onClick = { viewModel.toggleWatering() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = viewModel.getWateringButtonColor(),
                            contentColor = viewModel.getWateringButtonTextColor(),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = viewModel.getWateringButtonIcon(), contentDescription = null)
                            Text(
                                text = viewModel.getWateringButtonText(),
                                fontSize = TEXT_SIZE_SMALL,
                            )
                        }
                    }

                    Text(
                        text = stringResource(
                            id = R.string.current_watering_stats,
                            state.currentWateringVolume,
                            state.currentWateringDuration,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 10.dp, 0.dp, 10.dp),
                        textAlign = TextAlign.Center,
                        fontSize = TEXT_SIZE_NORMAL,
                        fontWeight = FontWeight.Normal,
                    )
                }

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 20.dp, 10.dp, 40.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.watering_presets),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 0.dp, 20.dp)
                            .weight(0.1f),
                        fontSize = TEXT_SIZE_BIG,
                        fontWeight = FontWeight.Medium,
                    )

                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight(align = Alignment.CenterVertically),
                            text = stringResource(id = R.string.enable_watering_presets),
                            fontSize = TEXT_SIZE_NORMAL,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Switch(
                            checked = state.isWateringProgramsEnabled,
                            onCheckedChange = { viewModel.toggleEnabledWateringPrograms() },
                        )
                    }

                    if (state.isWateringProgramsEnabled) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .wrapContentHeight(align = Alignment.CenterVertically),
                                text = stringResource(id = R.string.active_watering_preset),
                                fontSize = TEXT_SIZE_NORMAL,
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .wrapContentHeight(align = Alignment.CenterVertically),
                                text = viewModel.getCurrentWateringProgramName(),
                                fontSize = TEXT_SIZE_NORMAL,
                                fontWeight = FontWeight.Medium,
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f),
                            content = {
                                items(state.wateringPrograms.size) { index ->
                                    val wateringProgram = state.wateringPrograms[index]
                                    WateringProgramLine(wateringProgram = wateringProgram)

                                    if (index < state.wateringPrograms.size - 1)
                                        Divider(
                                            color = MaterialTheme.colorScheme.primary,
                                            thickness = 1.dp
                                        )
                                }
                            }
                        )

                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = { }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Text(
                                    text = stringResource(id = R.string.add_watering_preset),
                                    fontSize = TEXT_SIZE_SMALL,
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(0.7f))
                    }
                }
            }
        }
    }
}

