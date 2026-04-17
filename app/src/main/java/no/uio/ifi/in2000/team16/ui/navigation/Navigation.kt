package no.uio.ifi.in2000.team16.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoScreen
import no.uio.ifi.in2000.team16.ui.dev.DevScreen
import no.uio.ifi.in2000.team16.ui.dev.DevViewModel
import no.uio.ifi.in2000.team16.ui.map.MapScreen
import no.uio.ifi.in2000.team16.ui.map.MapViewModel
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreen
import no.uio.ifi.in2000.team16.ui.onboard.OnboardingScreen
import androidx.core.content.edit


//
const val MAP_SCREEN = "map"
const val DEV_SCREEN = "dev"

const val MUSHROOM_INFO_SCREEN = "mushroomInfo"

const val ONBOARDING_SCREEN = "onboarding"

const val PROFILE_SCREEN = "profileScreen"


@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val mapViewModel: MapViewModel = hiltViewModel()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val onboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != ONBOARDING_SCREEN

    Scaffold(
        bottomBar = {
            if(currentRoute!=ONBOARDING_SCREEN) {
                BottomNavigationBar(navController = navController)
            }}
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if(onboardingCompleted) MAP_SCREEN else ONBOARDING_SCREEN,  // egt MAP_SCREEN dette er kun test
            modifier = modifier.fillMaxSize()
                .padding(innerPadding)
        ){
            composable(ONBOARDING_SCREEN) {
                OnboardingScreen(
                    onFinished = {
                        sharedPreferences.edit {
                            putBoolean("onboarding_completed", true)
                        }
                        navController.navigate(MAP_SCREEN) {
                            popUpTo(ONBOARDING_SCREEN) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(MAP_SCREEN) {
                MapScreen(
                    onNavigateToDev = {
                        navController.navigate(DEV_SCREEN)
                    },
                    viewModel = mapViewModel,
                )
            }


            composable(PROFILE_SCREEN) {
                ProfileScreen(navController = navController)
            }

            composable(DEV_SCREEN) {
                val devViewModel: DevViewModel = hiltViewModel()

                DevScreen(
                    mapViewModel = mapViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(MUSHROOM_INFO_SCREEN) {
                MushroomInfoScreen(
                    onNavigateHome = {
                        navController.navigate(MAP_SCREEN) {
                            popUpTo(MAP_SCREEN) {
                                inclusive = false
                            }
                        }
                    }
                )
            }
        }
    }
}
















