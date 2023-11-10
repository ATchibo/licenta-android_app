package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.ui.components.addpage.QrScanner

@Composable
fun AddRpiPage() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            text = stringResource(id = R.string.add_device_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        )

//        QrScanner()

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            Text(text = stringResource(id = R.string.add_device_instructions_0))
            Text(text = stringResource(id = R.string.add_device_instructions_1))
            Text(text = stringResource(id = R.string.add_device_instructions_2))
            Text(text = stringResource(id = R.string.add_device_instructions_3))
        }
    }
}