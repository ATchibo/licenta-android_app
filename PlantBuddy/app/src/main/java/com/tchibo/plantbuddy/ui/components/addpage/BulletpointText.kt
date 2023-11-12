package com.tchibo.plantbuddy.ui.components.addpage

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tchibo.plantbuddy.utils.NORMAL_TEXT_SIZE

@Composable
fun BulletpointText(text: String) {
    val bulletPoint = "\u2022" // Unicode character for bullet point

    Text(
        text = "$bulletPoint $text",
        fontSize = NORMAL_TEXT_SIZE
    )
}