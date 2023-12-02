package com.tchibo.plantbuddy

import android.os.Bundle
import com.google.android.gms.auth.api.identity.Identity
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.PlantBuddyTheme
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.controller.db.LocalDbController
import com.tchibo.plantbuddy.ui.pages.AddRpiPage
import com.tchibo.plantbuddy.ui.pages.DetailsPage
import com.tchibo.plantbuddy.ui.pages.HomePage
import com.tchibo.plantbuddy.ui.pages.LoginPage
import com.tchibo.plantbuddy.ui.pages.SettingsPage
import com.tchibo.plantbuddy.utils.Routes
import com.tchibo.plantbuddy.utils.sign_in.GoogleAuthClient
import com.tchibo.plantbuddy.utils.sign_in.SignInViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

                val context = LocalContext.current
                LocalDbController.initialize(context)

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

    private suspend fun initialiseDbs() {
        FirebaseController.initialize(googleAuthClient.getSignedInUser()!!)
        LocalDbController.INSTANCE.loadInitialData()
    }

    @Composable
    fun ComposeNavigation() {

        val navController = LocalNavController.current

        // auto login when starting the app
        LaunchedEffect(key1 = Unit) {
            if (googleAuthClient.getSignedInUser() != null) {
                initialiseDbs()
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
                        initialiseDbs()
                        navController.navigate(Routes.getNavigateHome())
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
        }

    }
}

val LocalNavController = compositionLocalOf<NavHostController> { error("No NavController found!") }