package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tchibo.plantbuddy.utils.sign_in.UserData

@Composable
fun SettingsPage(
    userData: UserData,
    logout: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "Settings")

        Text(text = "User: ${userData.username}")

        Button(onClick = { logout() }) {
            Text(text = "Logout")
        }
    }
}