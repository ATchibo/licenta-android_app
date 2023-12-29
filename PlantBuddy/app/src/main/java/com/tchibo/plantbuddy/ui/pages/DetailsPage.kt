package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.domain.RaspberryStatus
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.ui.components.detailspage.HumidityGraph
import com.tchibo.plantbuddy.ui.viewmodels.DetailsPageViewmodel
import com.tchibo.plantbuddy.utils.TEXT_SIZE_BIG
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp, 0.dp, 0.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.loading),
                        fontSize = TEXT_SIZE_NORMAL,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
            } else {

                Text(
                    text = state.raspberryInfo.raspberryName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 10.dp, 0.dp, 0.dp),
                    fontSize = TEXT_SIZE_BIG,
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

                Box(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    HumidityGraph(
                        state = state
                    )
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
                                    modifier = Modifier
                                        .padding(0.dp, 0.dp, 0.dp, 0.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null)
                                    Text(
                                        text = "Watering options",
                                        fontSize = TEXT_SIZE_SMALL,
                                    )
                                }
                            }
                        }

                        item {
                            Button(
                                modifier = Modifier.padding(10.dp, 5.dp),
                                onClick = { /*TODO*/ }
                            ) {
                                Text(text = "Action 2")
                            }
                        }

                        item {
                            Button(
                                modifier = Modifier.padding(10.dp, 5.dp),
                                onClick = { /*TODO*/ }
                            ) {
                                Text(text = "Action 3")
                            }
                        }

                        item {
                            Button(
                                modifier = Modifier.padding(10.dp, 5.dp),
                                onClick = { /*TODO*/ }
                            ) {
                                Text(text = "Action 4")
                            }
                        }
                    }
                )

                Button(
                    modifier = Modifier
                        .padding(10.dp, 15.dp, 10.dp, 30.dp)
                        .fillMaxWidth(),
                    colors = buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    onClick = { /*TODO*/ }
                ) {
                    Text(
                        text = stringResource(id = R.string.unlink_device),
                        fontSize = TEXT_SIZE_SMALL,
                    )
                }
            }
        }
    }
}