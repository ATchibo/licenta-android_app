package com.tchibo.plantbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.PlantBuddyTheme
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.ui.pages.HomePage
import com.tchibo.plantbuddy.ui.pages.LoginPage
import com.tchibo.plantbuddy.ui.pages.RegisterPage
import com.tchibo.plantbuddy.utils.Routes
import com.tchibo.plantbuddy.utils.ScreenInfo

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlantBuddyTheme {
                val navController = rememberNavController()

                val (currentScreenInfo, setCurrentScreenInfo) = remember {
                    mutableStateOf(ScreenInfo())
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold (
                        topBar = { Appbar(currentScreenInfo) }
                    ) { paddingValues ->
                        CompositionLocalProvider (LocalNavController provides navController) {
                            ComposeNavigation(paddingValues, setCurrentScreenInfo)
                        }
                    }
                }
            }
        }
    }
}

val LocalNavController = compositionLocalOf<NavHostController> { error("No NavController found!") }

@Composable
fun ComposeNavigation(paddingValues: PaddingValues, setCurrentScreenInfo: (ScreenInfo) -> Unit) {

    val navController = LocalNavController.current

    NavHost(
        navController = navController,
        startDestination = Routes.getNavigateLogin(),
        modifier = Modifier.padding(paddingValues)
    ){
        composable(Routes.getNavigateLogin()){
            LoginPage(setCurrentScreenInfo)
        }
        composable(Routes.getNavigateRegister()){
            RegisterPage(setCurrentScreenInfo)
        }
        composable(Routes.getNavigateHome()){
            HomePage(setCurrentScreenInfo)
        }
//        composable(Routes.getNavigateDetailsRaw()){ backStackEntry ->
//            val taskJson = backStackEntry.arguments?.getString("taskId")
//            DetailsPage(Json.decodeFromString(taskJson.orEmpty()), setCurrentScreenInfo)
//        }
//        composable(Routes.getNavigateAdd()) {
//            AddTaskPage(setCurrentScreenInfo = setCurrentScreenInfo)
//        }
//        composable(Routes.getNavigateEditRaw()) {backStackEntry ->
//            val taskJson = backStackEntry.arguments?.getString("taskId")
//            EditTaskPage(taskJson.orEmpty().toInt(), setCurrentScreenInfo)
//        }
    }

}