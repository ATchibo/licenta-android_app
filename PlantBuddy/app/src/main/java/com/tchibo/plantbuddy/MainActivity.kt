package com.tchibo.plantbuddy

import android.app.Activity.RESULT_OK
import android.os.Bundle
import com.google.android.gms.auth.api.identity.Identity
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.PlantBuddyTheme
import com.tchibo.plantbuddy.ui.pages.AddRpiPage
import com.tchibo.plantbuddy.ui.pages.DetailsPage
import com.tchibo.plantbuddy.ui.pages.HomePage
import com.tchibo.plantbuddy.ui.pages.LoginPage
import com.tchibo.plantbuddy.ui.pages.SettingsPage
import com.tchibo.plantbuddy.utils.Routes
import com.tchibo.plantbuddy.utils.ScreenInfo
import com.tchibo.plantbuddy.utils.sign_in.GoogleAuthClient
import com.tchibo.plantbuddy.utils.sign_in.SignInViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {

    private val googleAuthClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlantBuddyTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    CompositionLocalProvider (LocalNavController provides navController) {
                        ComposeNavigation()
                    }
                }
            }
        }
    }

    @Composable
    fun ComposeNavigation() {

        val navController = LocalNavController.current

        // auto login when starting the app
        LaunchedEffect(key1 = Unit) {
            if (googleAuthClient.getSignedInUser() != null) {
                navController.navigate(Routes.getNavigateHome())
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
                        navController.navigate(Routes.getNavigateHome())
                        viewModel.resetState()
                    }
                }

                if (googleAuthClient.getSignedInUser() == null)
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
                AddRpiPage()
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
        }

    }
}

val LocalNavController = compositionLocalOf<NavHostController> { error("No NavController found!") }