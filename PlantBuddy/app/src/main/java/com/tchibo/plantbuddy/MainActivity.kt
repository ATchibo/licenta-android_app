package com.tchibo.plantbuddy

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.PlantBuddyTheme
import com.google.android.gms.auth.api.identity.Identity
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.ui.pages.AddProgramPage
import com.tchibo.plantbuddy.ui.pages.AddRpiPage
import com.tchibo.plantbuddy.ui.pages.DetailsPage
import com.tchibo.plantbuddy.ui.pages.HomePage
import com.tchibo.plantbuddy.ui.pages.LoginPage
import com.tchibo.plantbuddy.ui.pages.LoginRequestActivity
import com.tchibo.plantbuddy.ui.pages.LogsPage
import com.tchibo.plantbuddy.ui.pages.RaspberrySettingsPage
import com.tchibo.plantbuddy.ui.pages.SettingsPage
import com.tchibo.plantbuddy.ui.pages.WateringOptionsPage
import com.tchibo.plantbuddy.ui.viewmodels.SignInViewModel
import com.tchibo.plantbuddy.utils.GoogleAuthClient
import com.tchibo.plantbuddy.utils.Routes
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val googleAuthClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermissions()

        setContent {
            PlantBuddyTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    CompositionLocalProvider (LocalNavController provides navController) {
                        ComposeNavigation(intent)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        mainActivityIntent.putExtra("title", intent.extras?.getString("title"))
        mainActivityIntent.putExtra("body", intent.extras?.getString("body"))
        mainActivityIntent.putExtra("data", intent.extras?.getString("data"))

        val requestCode = System.currentTimeMillis().toInt()
        val pendingIntent : PendingIntent = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, requestCode, mainActivityIntent, PendingIntent.FLAG_MUTABLE)
        }else{
            PendingIntent.getActivity(this, requestCode, mainActivityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        pendingIntent.send()
    }

    private fun initialiseDbs() {
        FirebaseController.initialize(googleAuthClient.getSignedInUser()!!)
    }

    @Composable
    fun ComposeNavigation(intent: Intent?) {

        var routeAfterLogin = Routes.getNavigateHome()

        val intentData = intent?.extras?.getString("data")
        if (intentData != null) {
            val dataMap: Map<String, String> = Json.decodeFromString(intentData)

            when (dataMap["type"]) {
                "LOG" -> {
                    // do nothing
                }
                "LOGIN_REQUEST" -> {
                    routeAfterLogin = Routes.getNavigateLoginRequest()
                }
            }
        }

        val navController = LocalNavController.current

        val showLoading = remember {
            mutableStateOf(false)
        }

        // auto login when starting the app
        LaunchedEffect(key1 = Unit) {
            if (googleAuthClient.getSignedInUser() != null) {
                showLoading.value = true
                initialiseDbs()
                showLoading.value = false
                navController.navigate(routeAfterLogin) {
                    popUpTo(0)
                }
            }
        }

        NavHost(
            navController = navController,
            startDestination = Routes.getNavigateLogin(),
        ){
            composable(Routes.getNavigateLogin()){
                val viewModel = viewModel<SignInViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = {result ->
                        if (result.resultCode == RESULT_OK) {
                            lifecycleScope.launch {
                                val signInResult = googleAuthClient.signInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                viewModel.onSignInResult(signInResult)
                            }
                        }
                    }
                )

                // go to homepage if login is successful
                LaunchedEffect(key1 = state.isSignInSuccessful) {
                    if (state.isSignInSuccessful) {
                        showLoading.value = true
                        initialiseDbs()
                        showLoading.value = false

                        viewModel.registerUser(googleAuthClient.getSignedInUser())

                        navController.navigate(Routes.getNavigateHome()) {
                            popUpTo(0)
                        }
                        viewModel.resetState()
                    }
                }

                if (googleAuthClient.getSignedInUser() == null) {
                    LoginPage(
                        state = state,
                        logInFunction = {
                            lifecycleScope.launch {
                                val signInIntentSender = googleAuthClient.signIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        }
                    )
                } else {
                    if (showLoading.value) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            CircularProgressIndicator()

                            Text(
                                text = stringResource(id = R.string.loading),
                                modifier = Modifier.padding(start = 10.dp),
                                fontSize = TEXT_SIZE_NORMAL
                            )
                        }
                    }
                }
            }
            composable(Routes.getNavigateHome()){
                googleAuthClient.getSignedInUser()?.let { it1 ->
                    HomePage(
                        userData = it1,
                    )
                }
            }
            composable(Routes.getNavigateDetailsRaw()){ backStackEntry ->
                val rpiId = backStackEntry.arguments?.getString("id")
                DetailsPage(rpiId.orEmpty())
            }
            composable(Routes.getNavigateAdd()) {
                googleAuthClient.getSignedInUser()?.let { it1 ->
                    AddRpiPage(
                        userData = it1,
                    )
                }
            }
            composable(Routes.getNavigateSettings()) {
                googleAuthClient.getSignedInUser()?.let { it1 ->
                    SettingsPage(
                        userData = it1,
                        logout = {
                            lifecycleScope.launch {
                                googleAuthClient.signOut()
                                navController.navigate(Routes.getNavigateLogin())
                            }
                        }
                    )
                }
            }
            composable(Routes.getNavigateWateringOptionsRaw()) {
                val rpiId = it.arguments?.getString("id")
                WateringOptionsPage(raspberryPiId = rpiId.orEmpty())
            }
            composable(Routes.getNavigateAddProgramRaw()) {
                val rpiId = it.arguments?.getString("id").orEmpty()
                val programId = it.arguments?.getString("programId").orEmpty()
                AddProgramPage(raspberryId = rpiId, programId = programId)
            }
            composable(Routes.getNavigateLogsRaw()) {
                val rpiId = it.arguments?.getString("id").orEmpty()
                LogsPage(raspberryId = rpiId)
            }
            composable(Routes.getNavigateRaspberrySettingsRaw()) {
                val rpiId = it.arguments?.getString("id").orEmpty()
                RaspberrySettingsPage(raspberryId = rpiId)
            }
            composable(Routes.getNavigateLoginRequest()) {
                if (intent != null) {
                    LoginRequestActivity(intent = intent)
                }
            }
        }
    }

    private fun requestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }
}

val LocalNavController = compositionLocalOf<NavHostController> { error("No NavController found!") }