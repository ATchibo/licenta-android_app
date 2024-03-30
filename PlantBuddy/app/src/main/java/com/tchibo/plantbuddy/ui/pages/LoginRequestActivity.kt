package com.tchibo.plantbuddy.ui.pages

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.ui.components.ProgressIndicator
import com.tchibo.plantbuddy.ui.viewmodels.LoginActivityViewModel
import com.tchibo.plantbuddy.utils.Routes
import com.tchibo.plantbuddy.utils.TEXT_SIZE_SMALL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_UGE

@Composable
fun LoginRequestActivity (
    intent: Intent
) {

    val context = LocalContext.current
    val navigator = LocalNavController.current

    val viewModel = hiltViewModel<LoginActivityViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        if (intent.hasExtra("title") && intent.hasExtra("body")) {
            viewModel.loadData (
                title = intent.getStringExtra("title")!!,
                message = intent.getStringExtra("body")!!,
                data = intent.getStringExtra("data")!!,
                onFinished = {
                    navigator.navigate(Routes.getNavigateHome())
                }
            )
        }
    }

    LaunchedEffect(key1 = state.loginSuccessful) {
        if (state.loginSuccessful) {
            navigator.navigate(Routes.getNavigateHome())
        }
    }

    LaunchedEffect(key1 = state.toastMessage) {
        if (state.toastMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                state.toastMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {

        if (!state.loadedData) {
            ProgressIndicator()
            return
        }

        Text(
            text = state.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp, 10.dp, 30.dp, 0.dp),
            fontSize = TEXT_SIZE_UGE,
            fontWeight = FontWeight.Medium,
            color = Color.White,
        )

        Text(
            text = state.message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp, 10.dp, 30.dp, 30.dp),
            fontSize = TEXT_SIZE_SMALL,
            color = Color.White,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (state.connecting || state.loginSuccessful) {
            ProgressIndicator()
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 10.dp, 30.dp, 10.dp),
                onClick = {
                    viewModel.onTerminate()
                    navigator.navigate(Routes.getNavigateHome())
                }
            ) {
                Text(text = stringResource(id = R.string.go_to_home))
            }
        } else {

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 10.dp, 30.dp, 10.dp),
                onClick = { viewModel.onAcceptLogin() }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 10.dp, 30.dp, 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                onClick = {
                    viewModel.onDenyLogin()
                    navigator.navigate(Routes.getNavigateHome())
                }
            ) {
                Text(text = stringResource(id = R.string.no))
            }
        }
    }

//    if (state.showNotification) {
//        AlertDialog(
//            onDismissRequest = {},
//            title = {
//                Text(text = state.notificationTitle)
//            },
//            text = {
//                Text(text = state.notificationMessage)
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        viewModel.onAcceptLogin()
//                    }
//                ) {
//                    Text("OK")
//                }
//            },
//            dismissButton = {
//                Button(
//                    onClick = {
//                        viewModel.onDenyLogin()
//                    }
//                ) {
//                    Text("NO")
//                }
//            },
//        )
//    }
}