package com.tchibo.plantbuddy.ui.components.homepage

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.domain.RaspberryInfoDto
import com.tchibo.plantbuddy.domain.RaspberryStatus
import com.tchibo.plantbuddy.utils.Routes

@Composable
fun RaspberryShortcutCard(raspberryInfoDto: RaspberryInfoDto) {

    val navController = LocalNavController.current

    fun goToTaskDetails() {
        navController.navigate(
            Routes.getNavigateDetails(raspberryInfoDto.raspberryId),
        )
    }

    Log.d("RaspberryShortcutCard", "raspberryInfoDto: $raspberryInfoDto")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
            .padding(10.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(10.dp)
            .clickable { goToTaskDetails() }
    ) {
        Column {
            Text(
                text = raspberryInfoDto.raspberryName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            if (raspberryInfoDto.raspberryStatus != RaspberryStatus.NOT_COMPUTED) {
                Text(
                    text = stringResource(
                        id = R.string.raspberry_status,
                        raspberryInfoDto.raspberryStatus.toString()
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun ComposeLocalWrapper(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalNavController provides rememberNavController(),
        content = content
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RaspberryShortcutCardPreview() {
    val raspberryInfoDto = RaspberryInfoDto(
        raspberryId = "1",
        raspberryName = "Raspberry 1",
        raspberryStatus = RaspberryStatus.ONLINE,
    )

    ComposeLocalWrapper {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
        ) {
            RaspberryShortcutCard(raspberryInfoDto)
        }
    }
}
