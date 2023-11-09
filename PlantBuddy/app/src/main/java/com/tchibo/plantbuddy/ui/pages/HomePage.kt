package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.domain.RaspberryDataDto
import com.tchibo.plantbuddy.domain.RaspberryStatus
import com.tchibo.plantbuddy.temp.TempDb
import com.tchibo.plantbuddy.ui.components.homepage.RaspberryShortcutCard
import com.tchibo.plantbuddy.utils.ScreenInfo

@Composable
fun HomePage() {

    var raspberryDtoList = remember {
        mutableStateOf(TempDb.getMyRaspberryDtoItems())
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(MaterialTheme.colorScheme.background),
    ) {
        Column {
            Text(
                text = stringResource(
                    id = R.string.main_screen_title,
                    "Tchibo" // temp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                fontSize = 20.sp,
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                content = {
                    items(raspberryDtoList.value.size) { index ->
                        RaspberryShortcutCard(raspberryDtoList.value[index])
                    }
                }
            )
        }
    }
}
