package com.tchibo.plantbuddy.ui.pages

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.ui.components.addpage.BulletpointText
import com.tchibo.plantbuddy.ui.components.addpage.QrScanner
import com.tchibo.plantbuddy.utils.BIG_TEXT_SIZE
import com.tchibo.plantbuddy.utils.NORMAL_TEXT_SIZE

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddRpiPage() {

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            text = stringResource(id = R.string.add_device_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 50.dp),
            fontSize = BIG_TEXT_SIZE
        )

        QrScanner()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.add_device_instructions_0),
                    modifier = Modifier.padding(bottom = 10.dp),
                    fontSize = NORMAL_TEXT_SIZE
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

        if (!cameraPermissionState.status.isGranted) {
            Column {
                val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                    "The camera is important for this app. Please grant the permission."
                } else {
                    "Camera permission required for this feature to be available. " +
                            "Please grant the permission"
                }
                Toast.makeText(LocalContext.current, textToShow, Toast.LENGTH_SHORT).show()
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Request permission")
                }
            }
        }
    }
}

