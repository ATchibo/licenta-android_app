package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.tchibo.plantbuddy.domain.RaspberryStatus
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.ui.components.ProgressIndicator
import com.tchibo.plantbuddy.ui.components.detailspage.HumidityGraph
import com.tchibo.plantbuddy.ui.viewmodels.DetailsPageViewmodel
import com.tchibo.plantbuddy.utils.TEXT_SIZE_UGE
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_SMALL

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
            if (state.isRefreshing) {
                ProgressIndicator()
            } else {

                Text(
                    text = state.raspberryInfo.raspberryName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 10.dp, 0.dp, 0.dp),
                    fontSize = TEXT_SIZE_UGE,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )

                Text(
                    text = state.raspberryInfo.raspberryStatus.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 10.dp, 0.dp, 20.dp),
                    fontSize = TEXT_SIZE_NORMAL,
                    fontWeight = FontWeight.Medium,
                    color = when (state.raspberryInfo.raspberryStatus) {
                        RaspberryStatus.ONLINE -> MaterialTheme.colorScheme.primary
                        RaspberryStatus.OFFLINE -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onBackground
                    },
                )

                Text(
                    text = stringResource(id = R.string.moisture_history),
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth(),
                    fontSize = TEXT_SIZE_NORMAL,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                )

                Box(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    if (state.isGraphRefreshing) {
                        ProgressIndicator()
                    } else {
                        HumidityGraph(
                            state = state
                        )
                    }
                }

                Column (
                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // TODO: actual update hour
                    Text(
                        text = stringResource(
                            id = R.string.last_update,
                            state.lastUpdatedTime.value
                        ),
                        modifier = Modifier
                            .padding(0.dp, 10.dp, 0.dp, 10.dp),
                        fontSize = TEXT_SIZE_SMALL,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )

                    Button(onClick = { viewModel.updateHumidityValuesList() }) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Text(
                                text = stringResource(id = R.string.update),
                                fontSize = TEXT_SIZE_SMALL,
                            )
                        }
                    }

                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 10.dp, 0.dp),
                            text = stringResource(id = R.string.change_period),
                        )

                        Column {
                            Button(
                                onClick = {
                                    viewModel.toggleHumidityDropdown()
                                }
                            ) {
                                Text(
                                    text = viewModel.getCurrentHumidityDropdownOption()
                                )
                            }

                            DropdownMenu(
                                expanded = state.isHumidityDropdownExpanded.value,
                                onDismissRequest = {
                                    viewModel.closeHumidityDropdown()
                                }
                            ) {

                                state.humidityDropdownOptions.forEachIndexed { index, s ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = s,
                                                fontSize = TEXT_SIZE_SMALL,
                                            )
                                        },
                                        onClick = {
                                            viewModel.onHumidityDropdownOptionSelected(index)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    content = {
                        item {
                            Button(
                                modifier = Modifier.padding(10.dp, 5.dp),
                                onClick = { viewModel.goToWateringOptions() }
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null)
                                    Text(
                                        text = stringResource(id = R.string.watering_options),
                                        fontSize = TEXT_SIZE_SMALL,
                                    )
                                }
                            }
                        }

                        item {
                            Button(
                                modifier = Modifier.padding(10.dp, 5.dp),
                                onClick = { viewModel.goToLogs() }
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.FormatListNumbered, contentDescription = null)
                                    Text(
                                        text = stringResource(id = R.string.view_logs),
                                        fontSize = TEXT_SIZE_SMALL,
                                    )
                                }
                            }
                        }

                        item {
                            Button(
                                modifier = Modifier.padding(10.dp, 5.dp),
                                onClick = { viewModel.goToRaspberrySettings() }
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                                    Text(
                                        text = stringResource(id = R.string.device_settings),
                                        fontSize = TEXT_SIZE_SMALL,
                                    )
                                }
                            }
                        }

                        item {
                            Button(
                                modifier = Modifier.padding(10.dp, 5.dp),
                                colors = buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                onClick = { viewModel.unlinkRaspberry() }
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.LinkOff, contentDescription = null)
                                    Text(
                                        text = stringResource(id = R.string.unlink_device),
                                        fontSize = TEXT_SIZE_SMALL,
                                    )
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}