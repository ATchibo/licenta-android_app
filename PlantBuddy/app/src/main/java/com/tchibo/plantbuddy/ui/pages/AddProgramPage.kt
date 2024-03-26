package com.tchibo.plantbuddy.ui.pages

import android.app.TimePickerDialog
import android.widget.Toast
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProgramPage (
    raspberryId: String,
    programId: String
) {

    val navigator = LocalNavController.current
    val viewModel = viewModel<ProgramViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProgramViewModel(navigator, raspberryId, programId) as T
            }
        }
    )

    val state = viewModel.state.value

    val context = LocalContext.current

    val timePickerDialog =
        TimePickerDialog (
            context,
            { _, hour, minute -> viewModel.onTimeOfDayChanged(hour, minute) },
            viewModel.getTimeOfDayHour(),
            viewModel.getTimeOfDayMinute(),
            true
        )

    val date = remember {
        Calendar.getInstance().timeInMillis
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date,
        yearRange = Calendar.getInstance().get(Calendar.YEAR)..Calendar.getInstance().get(Calendar.YEAR) + 2
    )
    var showDatePicker by remember {
        mutableStateOf(false)
    }

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

                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 5.dp)
                        ) {
                            Button(
                                onClick = { showDatePicker = true },
                                modifier = Modifier
                                    .padding(0.dp, 5.dp)
                            ) {
                                Text(
                                    text = viewModel.getSelectedDate(),
                                    fontSize = TEXT_SIZE_SMALL,
                                )
                            }

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

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = {},
                    confirmButton = {
                        Button(
                            onClick = {
                                val selectedDate = Calendar.getInstance().apply {
                                    timeInMillis = datePickerState.selectedDateMillis!!
                                }
                                if (viewModel.isDateValid(selectedDate)) {
                                    Toast.makeText(
                                        context,
                                        "Saved date",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    showDatePicker = false
                                    viewModel.onDateChanged(selectedDate)

                                } else {
                                    Toast.makeText(
                                        context,
                                        "Invalid date",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDatePicker = false
                            }
                        ) { Text("Cancel") }
                    }
                )
                {
                    DatePicker(state = datePickerState)
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