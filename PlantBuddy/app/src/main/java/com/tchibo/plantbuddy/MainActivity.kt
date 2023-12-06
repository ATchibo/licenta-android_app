package com.tchibo.plantbuddy

import android.os.Bundle
import com.google.android.gms.auth.api.identity.Identity
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL
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

        val showLoading = remember {
            mutableStateOf(false)
        }

        // auto login when starting the app
        LaunchedEffect(key1 = Unit) {
            if (googleAuthClient.getSignedInUser() != null) {
                showLoading.value = true
                initialiseDbs()
                showLoading.value = false
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
                        showLoading.value = true
                        initialiseDbs()
                        showLoading.value = false
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
                } else {
                    if (showLoading.value) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            CircularProgressIndicator()

                            Text(
                                text = stringResource(id = R.string.initial_loading),
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
        }

    }
}

val LocalNavController = compositionLocalOf<NavHostController> { error("No NavController found!") }