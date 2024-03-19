package com.tchibo.plantbuddy.ui.components.raspberrysettingspage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL

@Composable
fun NotifiableMessageComponent (
    notifiableMessageName: String,
    notifiableMessageValue: Boolean,
    onNotifiableMessageValueChange: (Boolean, (Boolean) -> Unit) -> Unit,
) {

    var mutableNotifiableMessageValue by remember {
        mutableStateOf(notifiableMessageValue)
    }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(0.dp, 5.dp)
    ) {

        Text(
            text = notifiableMessageName,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .weight(0.8f),
            fontSize = TEXT_SIZE_NORMAL,
            color = Color.White,
        )

        Switch(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.2f),
            checked = mutableNotifiableMessageValue,
            onCheckedChange =  {
                onNotifiableMessageValueChange(it) { newValue ->
                    mutableNotifiableMessageValue = newValue
                }
            },
        )
    }
}

@Preview
@Composable
fun NotifiableMessageComponentPreview() {
    Column (
        modifier = Modifier
            .padding(10.dp)
            .background(Color.Blue)
    ) {
        NotifiableMessageComponent(
            notifiableMessageName = "Notifiable Message",
            notifiableMessageValue = true,
            onNotifiableMessageValueChange = { _, _ -> }
        )

        NotifiableMessageComponent(
            notifiableMessageName = "asdsad Message",
            notifiableMessageValue = true,
            onNotifiableMessageValueChange = { _, _ -> }
        )
    }
}