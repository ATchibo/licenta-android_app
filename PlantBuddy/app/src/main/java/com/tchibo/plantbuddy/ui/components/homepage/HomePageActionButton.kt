package com.tchibo.plantbuddy.ui.components.homepage

import android.service.autofill.OnClickAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun HomePageActionButton(onClickAction: () -> Unit) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary,
        onClick = onClickAction
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add"
        )
    }
}