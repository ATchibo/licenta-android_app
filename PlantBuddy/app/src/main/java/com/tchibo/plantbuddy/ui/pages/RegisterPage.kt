package com.tchibo.plantbuddy.ui.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.utils.Routes
import com.tchibo.plantbuddy.utils.ScreenInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(setCurrentScreenInfo: (ScreenInfo) -> Unit) {

    setCurrentScreenInfo(
        ScreenInfo(
        title = "TaskScheduler"
    ))

    val navigator = LocalNavController.current
    val context = LocalContext.current

    val (loginValue, setLoginValue) = remember {
        mutableStateOf("")
    }

    val (pwValue, setPwValue) = remember {
        mutableStateOf("")
    }

    fun register() {
        Toast.makeText(context, "Registered!", Toast.LENGTH_SHORT).show()
        navigator.navigate(Routes.getNavigateLogin())
    }

    @Composable
    fun validateLogin(ok: Boolean) {
        if (!ok)
            Text(text = "You should have at least 3 characters")
    }

    @Composable
    fun validatePw(ok: Boolean) {
        if (!ok)
            Text(text = "You should have at least 3 characters")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            fontSize = 30.sp,
            modifier = Modifier.padding(10.dp)
        )
        TextField(value = loginValue, onValueChange = setLoginValue, label = {Text("Login")},            isError = loginValue.length < 3,
            supportingText = { validateLogin(loginValue.length >= 3)}, modifier = Modifier.padding(10.dp))
        TextField(value = pwValue, onValueChange = setPwValue, label = {Text("Password")},            isError = pwValue.length < 3,
            supportingText = { validatePw(pwValue.length >= 3)}, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.padding(10.dp))

        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { register() }, modifier = Modifier.padding(10.dp), enabled = loginValue.length >= 3 && pwValue.length >= 3) {
            Text(text = "Register")
        }
    }
}