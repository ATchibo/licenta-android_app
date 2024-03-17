@file:OptIn(ExperimentalMaterialApi::class)

package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.tchibo.plantbuddy.ui.components.ProgressIndicator
import com.tchibo.plantbuddy.ui.components.wateringpage.WateringProgramLine
import com.tchibo.plantbuddy.ui.viewmodels.WateringOptionsViewModel
import com.tchibo.plantbuddy.utils.TEXT_SIZE_BIG
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_SMALL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_UGE

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = viewModel::reloadWateringPrograms
    )

    Scaffold (
        topBar = { Appbar(screenInfo = state.screenInfo) },
    ) { paddingValues ->
        Box (
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(paddingValues = paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            Column {
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
                        .padding(30.dp, 10.dp, 30.dp, 30.dp),
                    fontSize = TEXT_SIZE_SMALL,
                    color = Color.White,
                )

                if (state.isLoadingInitData) {
                    ProgressIndicator()
                } else {
                    Spacer(modifier = Modifier.weight(1f))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 10.dp)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        // section header

                        Text(
                            text = stringResource(id = R.string.manual_watering),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 0.dp, 0.dp, 20.dp),
                            fontSize = TEXT_SIZE_BIG,
                            fontWeight = FontWeight.Medium,
                        )

                        // manual moisture check

                        Button(
                            onClick = { viewModel.checkMoisture() },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null
                                )
                                Text(
                                    text = stringResource(id = R.string.check_moisture),
                                    fontSize = TEXT_SIZE_SMALL,
                                )
                            }
                        }

                        Text(
                            text = stringResource(
                                id = R.string.moisture_level,
                                state.currentMoistureLevel
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 10.dp, 0.dp, 10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = TEXT_SIZE_NORMAL,
                            fontWeight = FontWeight.Normal,
                        )

                        // watering controls

                        Button(
                            onClick = { viewModel.toggleWatering() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = viewModel.getWateringButtonColor(),
                                contentColor = viewModel.getWateringButtonTextColor(),
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = viewModel.getWateringButtonIcon(),
                                    contentDescription = null
                                )
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 10.dp, 10.dp, 30.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.watering_presets),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 0.dp, 0.dp, 15.dp)
                                .weight(0.2f),
                            fontSize = TEXT_SIZE_BIG,
                            fontWeight = FontWeight.Medium,
                        )

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
                                    .padding(0.dp, 5.dp)
                                    .weight(0.15f),
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
                                        WateringProgramLine(
                                            index = index,
                                            wateringProgram = wateringProgram,
                                            onTap = viewModel::onWateringProgramTap,
                                            onEdit = { }
                                        )

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
                                onClick = {
                                    viewModel.goToAddWateringProgram()
                                }
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

                    if (state.isWateringProgramInfoPopupOpen) {
                        AlertDialog(
                            onDismissRequest = {
                                viewModel.closeWateringProgramInfoPopup()
                            },
                            title = {
                                viewModel.getPreviewWateringProgram()?.let {
                                    Text(
                                        text = it.getName(),
                                        fontSize = TEXT_SIZE_NORMAL,
                                    )
                                }
                            },
                            text = {
                                viewModel.getPreviewWateringProgram()?.let {
                                    Text(
                                        text = it.toStringBody(),
                                        fontSize = TEXT_SIZE_SMALL,
                                    )
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = {
                                        viewModel.closeWateringProgramInfoPopup()
                                    }
                                ) {
                                    Text(text = stringResource(id = R.string.cancel))
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.selectWateringOption(state.previewWateringOptionIndex)
                                        viewModel.closeWateringProgramInfoPopup()
                                    }
                                ) {
                                    Text(text = stringResource(id = R.string.set_program))
                                }
                            }
                        )
                    }
                }

            }

            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

