package com.tchibo.plantbuddy.ui.pages

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.domain.FirebaseDeviceLinking
import com.tchibo.plantbuddy.ui.components.addpage.BulletpointText
import com.tchibo.plantbuddy.ui.components.addpage.QrScanner
import com.tchibo.plantbuddy.utils.FirebaseController
import com.tchibo.plantbuddy.utils.Routes
import com.tchibo.plantbuddy.utils.TEXT_SIZE_BIG
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL
import com.tchibo.plantbuddy.utils.sign_in.UserData

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddRpiPage(
    userData: UserData
) {

    val navigator = LocalNavController.current
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val showCameraAlert = remember {
        mutableStateOf(!cameraPermissionState.status.isGranted)
    }

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
            fontSize = TEXT_SIZE_BIG
        )

        QrScanner(
            onQrCodeFound = {qrCode ->
                if (qrCode.isEmpty())
                    return@QrScanner

                val firebaseDeviceLinking = FirebaseDeviceLinking(qrCode, userData.email)
                FirebaseController.INSTANCE.addDeviceAccountLink(firebaseDeviceLinking, context) {
                    // TODO: Add error handling and change route
                    navigator.navigate(Routes.getNavigateHome())
                }
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
    }
}

