package com.tchibo.plantbuddy.ui.pages;

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.utils.Routes
import com.tchibo.plantbuddy.utils.ScreenInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(setCurrentScreenInfo: (ScreenInfo) -> Unit) {
    setCurrentScreenInfo(
        ScreenInfo(
            title = stringResource(id = R.string.app_name)
        )
    )

    val navigator = LocalNavController.current

    var loginValue by remember { mutableStateOf("") }
    var pwValue by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    fun login() {
        if (loginValue == "test" && pwValue == "test")
            navigator.navigate(Routes.getNavigateHome())
        else {
            showErrorDialog = true
        }
    }

    fun register() {
        navigator.navigate(Routes.getNavigateRegister())
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text(text = stringResource(id = R.string.error_title)) },
            text = { Text(text = stringResource(id = R.string.error_message)) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.login),
                fontSize = 30.sp,
                modifier = Modifier.padding(10.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = loginValue,
                onValueChange = { loginValue = it },
                label = { Text(text = stringResource(id = R.string.login)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface)
            )

            TextField(
                value = pwValue,
                onValueChange = { pwValue = it },
                label = { Text(text = stringResource(id = R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {login()},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Text(text = stringResource(id = R.string.login))
            }

            val registerText = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        textDecoration = TextDecoration.None
                    ),
                ) {
                    append("Don't have an account? ")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Register here")
                }
            }

            ClickableText(
                text = registerText,
                onClick = {register()},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}