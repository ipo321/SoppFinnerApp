package no.uio.ifi.in2000.team16.ui.navigation

import OnboardingScreen
import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team16.ui.dev.DevScreen
import no.uio.ifi.in2000.team16.ui.dev.DevViewModel
import no.uio.ifi.in2000.team16.ui.map.MapScreen
import no.uio.ifi.in2000.team16.ui.map.MapViewModel
import no.uio.ifi.in2000.team16.ui.minSide.HistoryScreen
import no.uio.ifi.in2000.team16.ui.minSide.InfoScreen
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreen
import no.uio.ifi.in2000.team16.ui.minSide.SettingsScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomBankScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomControlScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomSpotScreen
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoScreen
import androidx.core.content.edit
import no.uio.ifi.in2000.team16.ui.onboard.WelcomeScreen

const val MAP_SCREEN = "map"
const val DEV_SCREEN = "dev"
const val MUSHROOM_INFO_SCREEN = "mushroomInfo"
const val ONBOARDING_SCREEN = "onboarding"
const val PROFILE_SCREEN = "profileScreen"
const val SETTINGS_SCREEN = "profileScreen/settings"
const val HISTORY_SCREEN = "profileScreen/history"
const val SHROOMSPOT_SCREEN = "profileScreen/shroomSpots"
const val INFO_SCREEN = "profileScreen/info"
const val SHROOMCONTROL_SCREEN = "profileScreen/shroomControll"
const val SHROOMBANK_SCREEN = "profileScreen/shroomBank"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    //navController: NavHostController = rememberNavController()
    navController: NavHostController,
) {
    val mapViewModel: MapViewModel = hiltViewModel()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val onboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != ONBOARDING_SCREEN) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = if (onboardingCompleted) MAP_SCREEN else ONBOARDING_SCREEN,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        )

        /*{
            composable(ONBOARDING_SCREEN) {
                WelcomeScreen(
                    navController = navController
                )
            }


         */

        {composable(route = Screen.Welcome.route) {
                WelcomeScreen(navController = navController)
            }
/*
            composable(ONBOARDING_SCREEN) {
                OnboardingScreen(
                    onFinished = {
                        completed ->
                        if (completed) {
                            sharedPreferences.edit()
                                .putBoolean("onboarding_completed", true)
                                .apply()
                        }

                        navController.navigate(MAP_SCREEN) {
                            popUpTo(ONBOARDING_SCREEN) {
                                inclusive = true
                            }
                        }
                    }
                )
            }


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


 */
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

            composable(SETTINGS_SCREEN) {
                SettingsScreen(navController = navController)
            }

            composable(HISTORY_SCREEN) {
                HistoryScreen(navController = navController)
            }

            composable(SHROOMSPOT_SCREEN) {
                ShroomSpotScreen(navController = navController)
            }

            composable(INFO_SCREEN) {
                InfoScreen(navController = navController)
            }

            composable(SHROOMCONTROL_SCREEN) {
                ShroomControlScreen(navController = navController)
            }

            composable(SHROOMBANK_SCREEN) {
                ShroomBankScreen(navController = navController)
            }

            composable(DEV_SCREEN) {
                hiltViewModel<DevViewModel>()

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