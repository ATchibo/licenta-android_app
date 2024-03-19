package com.tchibo.plantbuddy.ui.components.logspage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tchibo.plantbuddy.utils.TEXT_SIZE_SMALL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LogCard(
    log: Pair<LocalDateTime, String>
) {
    val dateTime = DateTimeFormatter.ofPattern("MMMM dd, yyyy | hh:mm:ss").format(log.first)

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 5.dp)
    ) {
        Text(
            text = dateTime,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp, 5.dp),
            fontSize = TEXT_SIZE_SMALL,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )

        Text(
            text = log.second,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp, 5.dp),
            fontSize = TEXT_SIZE_SMALL,
            color = Color.White,
        )
    }
}