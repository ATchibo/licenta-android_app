package com.tchibo.plantbuddy.ui.pages

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.temp.TempDb
import com.tchibo.plantbuddy.ui.components.homepage.RaspberryShortcutCard

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
        Image(
            painterResource(R.drawable.home_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillHeight,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0x99000000))
                .padding(5.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = stringResource(
                    id = R.string.main_screen_title,
                    "Tchibo" // temp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
//                color = MaterialTheme.colorScheme.onSurface,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(id = R.string.main_screen_subtitle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp),
                fontSize = 20.sp,
//                color = MaterialTheme.colorScheme.onSurface,
                color = Color.White,
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
