package com.tchibo.plantbuddy.ui.pages

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.domain.UserData
import com.tchibo.plantbuddy.ui.components.addpage.BulletpointText
import com.tchibo.plantbuddy.ui.components.addpage.QrScanner
import com.tchibo.plantbuddy.ui.viewmodels.AddRpiPageViewmodel
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_UGE

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddRpiPage(
    userData: UserData
) {

    val navigator = LocalNavController.current

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val showCameraAlert = remember {
        mutableStateOf(!cameraPermissionState.status.isGranted)
    }

    val viewModel = viewModel<AddRpiPageViewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddRpiPageViewmodel(navigator, userData) as T
            }
        }
    )
    val state = viewModel.state.value


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            text = stringResource(id = R.string.add_device_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            fontSize = TEXT_SIZE_UGE
        )

        QrScanner(
            onQrCodeFound = {qrCode ->
                viewModel.onQrCodeRead(qrCode)
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.add_device_instructions_0),
                    modifier = Modifier.padding(bottom = 10.dp),
                    fontSize = TEXT_SIZE_NORMAL
                )
            }
            items(3) { index ->
                val instruction = stringResource(
                    when (index) {
                        0 -> R.string.add_device_instructions_1
                        1 -> R.string.add_device_instructions_2
                        2 -> R.string.add_device_instructions_3
                        else -> R.string.add_device_instructions_0
                    }
                )

                BulletpointText(instruction)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navigator.popBackStack()
            }
        ) {
            Text(text = stringResource(id = R.string.back_button_text))
        }

        if (showCameraAlert.value) {
            AlertDialog(
                onDismissRequest = {
                    showCameraAlert.value = false
                },
                title = {
                    Text(text = stringResource(id = R.string.camera_alert_title))
                },
                text = {
                    Text(text = stringResource(id = R.string.camera_alert_body))
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showCameraAlert.value = false
                            navigator.popBackStack()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.no))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showCameraAlert.value = false
                            cameraPermissionState.launchPermissionRequest()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            )
        }

        if (state.processingQrCode) {
            Dialog(
                onDismissRequest = { viewModel.stopProcessingQrCode() },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Row(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp))
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()

                    Text(
                        text = stringResource(id = R.string.add_device_loading),
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = TEXT_SIZE_NORMAL
                    )
                }
            }
        }
    }
}

