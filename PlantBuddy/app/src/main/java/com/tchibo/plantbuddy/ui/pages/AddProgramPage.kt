package com.tchibo.plantbuddy.ui.pages

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.ui.viewmodels.ProgramViewModel
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_SMALL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_UGE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProgramPage (
    raspberryId: String,
) {

    val navigator = LocalNavController.current
    val viewModel = viewModel<ProgramViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProgramViewModel(navigator, raspberryId) as T
            }
        }
    )

    val state = viewModel.state.value

    val timePickerState = rememberTimePickerState()
    val context = LocalContext.current

    val timePickerDialog =
        TimePickerDialog (
            context,
            { _, hour, minute -> viewModel.onTimeOfDayChanged(hour, minute) },
            viewModel.getTimeOfDayHour() / 60,
            viewModel.getTimeOfDayMinute() % 60,
            true
        )

    Scaffold (
        topBar = { Appbar(screenInfo = state.screenInfo) }
    ) { paddingValues ->

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(10.dp)
        ) {

            Text(
                text = stringResource(id = R.string.add_watering_preset),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 10.dp, 30.dp, 0.dp)
                    .weight(0.1f),
                fontSize = TEXT_SIZE_UGE,
                fontWeight = FontWeight.Medium,
                color = Color.White,
            )

            LazyColumn (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 10.dp)
                    .weight(0.8f)
            ) {

                item {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onNameChanged(it) },
                        label = { Text(stringResource(id = R.string.name)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 5.dp),
                        singleLine = true,
                        isError = !viewModel.isNameValid()
                    )
                }

                item {
                    OutlinedTextField(
                        value = state.frequencyDays,
                        onValueChange = { viewModel.onFrequencyDaysChanged(it) },
                        label = { Text(stringResource(id = R.string.frequency_days)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 5.dp),
                        singleLine = true,
                        isError = !viewModel.isFrequencyDaysValid(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = state.quantityL,
                        onValueChange = { viewModel.onQuantityLChanged(it) },
                        label = { Text(stringResource(id = R.string.quantity_l)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 5.dp),
                        singleLine = true,
                        isError = !viewModel.isQuantityLValid(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                }

                item {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 5.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.time_of_day_min),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 5.dp),
                            fontSize = TEXT_SIZE_NORMAL,
                        )
                        Button(
                            onClick = { timePickerDialog.show() },
                            modifier = Modifier
                                .padding(0.dp, 5.dp)
                        ) {
                            Text(
                                text = viewModel.getTimeOfDayString(),
                                fontSize = TEXT_SIZE_SMALL,
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = state.minMoisture,
                        onValueChange = { viewModel.onMinMoistureChanged(it) },
                        label = { Text(stringResource(id = R.string.min_moisture)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 5.dp),
                        singleLine = true,
                        isError = !viewModel.isMinMoistureValid(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = state.maxMoisture,
                        onValueChange = { viewModel.onMaxMoistureChanged(it) },
                        label = { Text(stringResource(id = R.string.max_moisture)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 5.dp),
                        singleLine = true,
                        isError = !viewModel.isMaxMoistureValid(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 5.dp),
                onClick = { viewModel.onCancelButtonClicked() },
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = null)
                    Text(
                        text = stringResource(id = R.string.cancel),
                        fontSize = TEXT_SIZE_SMALL,
                    )
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 5.dp),
                onClick = { viewModel.onSaveButtonClicked() },
                enabled = viewModel.isFormValid()
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Text(
                        text = stringResource(id = R.string.save),
                        fontSize = TEXT_SIZE_SMALL,
                    )
                }
            }
        }
    }
}