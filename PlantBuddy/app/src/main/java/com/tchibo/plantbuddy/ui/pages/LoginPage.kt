package com.tchibo.plantbuddy.ui.pages;

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.utils.Routes
import com.tchibo.plantbuddy.utils.ScreenInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(setCurrentScreenInfo: (ScreenInfo) -> Unit) {
    setCurrentScreenInfo(
        ScreenInfo(
            title = "PlantBuddy"
        )
    )

    val navigator = LocalNavController.current
    val openAlertDialog = remember { mutableStateOf(false) }

    val (loginValue, setLoginValue) = remember {
        mutableStateOf("")
    }

    val (pwValue, setPwValue) = remember {
        mutableStateOf("")
    }

    fun login() {
        if (loginValue == "test" && pwValue == "test")
            navigator.navigate(Routes.getNavigateHome())
        else {
            openAlertDialog.value = true
        }
    }

    fun register() {
        navigator.navigate(Routes.getNavigateRegister())
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            fontSize = 30.sp,
            modifier = Modifier.padding(10.dp)
        )

        TextField(value = loginValue, onValueChange = setLoginValue, label = {Text("Login")}, modifier = Modifier.padding(10.dp))
        TextField(value = pwValue, onValueChange = setPwValue, label = {Text("Password")}, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.padding(10.dp))

        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { register() }, modifier = Modifier.padding(10.dp)) {
            Text(text = "Register")
        }
        Button(onClick = { login() }, modifier = Modifier.padding(10.dp)) {
            Text(text = "Login")
        }
    }
}