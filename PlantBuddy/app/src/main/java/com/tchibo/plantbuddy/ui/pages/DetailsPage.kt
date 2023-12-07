package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.controller.db.LocalDbController
import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.ScreenInfo
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.utils.TEXT_SIZE_BIG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsPage(rpiId: String) {

    val navigator = LocalNavController.current

    val screenInfo = ScreenInfo(
        navigationIcon = Icons.Filled.ArrowBack,
        onNavigationIconClick = {
            navigator.popBackStack()
        },
    )

    val raspberyInfo = remember {
        mutableStateOf(RaspberryInfo())
    }

    LaunchedEffect(key1 = Unit) {
        raspberyInfo.value = LocalDbController.INSTANCE.getRaspberryInfo(rpiId) ?: RaspberryInfo()
    }
    
    Scaffold (
        topBar = { Appbar(screenInfo = screenInfo) }
    ) { paddingValues ->  
        Column (
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            Text(
                text = raspberyInfo.value.raspberryName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                fontSize = TEXT_SIZE_BIG,
                fontWeight = FontWeight.Medium,
                color = Color.White,
            )


        }
    }
}