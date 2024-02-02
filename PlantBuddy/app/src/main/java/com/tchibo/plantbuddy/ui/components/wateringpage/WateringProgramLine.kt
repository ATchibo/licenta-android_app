package com.tchibo.plantbuddy.ui.components.wateringpage

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tchibo.plantbuddy.domain.WateringProgram
import com.tchibo.plantbuddy.utils.TEXT_SIZE_SMALL

@Composable
fun WateringProgramLine(wateringProgram: WateringProgram) {

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(0.8f),
            text = wateringProgram.getName(),
            fontSize = TEXT_SIZE_SMALL,
        )

        IconButton(
            modifier = Modifier.weight(0.2f),
            onClick = { /*TODO*/ }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
            )
        }
    }
}
