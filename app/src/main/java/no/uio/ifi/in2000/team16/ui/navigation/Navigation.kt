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
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoScreen
import no.uio.ifi.in2000.team16.ui.dev.DevScreen
import no.uio.ifi.in2000.team16.ui.dev.DevViewModel
import no.uio.ifi.in2000.team16.ui.map.MapScreen
import no.uio.ifi.in2000.team16.ui.map.MapViewModel
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoViewModel
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomListScreen
import no.uio.ifi.in2000.team16.ui.minSide.HistoryScreen
import no.uio.ifi.in2000.team16.ui.minSide.InfoScreen
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreen
import no.uio.ifi.in2000.team16.ui.onboard.OnboardingScreen
import androidx.core.content.edit
import no.uio.ifi.in2000.team16.ui.minSide.SettingsScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomBankScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomControlScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomSpotScreen
import no.uio.ifi.in2000.team16.ui.popUp.StartPopUp



//
const val MAP_SCREEN = "map"
const val DEV_SCREEN = "dev"

const val MUSHROOM_INFO_SCREEN = "mushroomInfo"

const val ONBOARDING_SCREEN = "onboarding"

const val MUSHROOM_LIST_SCREEN = "mushroomList"
const val PROFILE_SCREEN = "profileScreen"

const val SETTINGS_SCREEN = "profileScreen/settings"
const val HISTORY_SCREEN = "profileScreen/history"
const val SHROOMSPOT_SCREEN = "profileScreen/shroomSpots"
const val INFO_SCREEN = "profileScreen/info"
const val SHROOMCONTROL_SCREEN = "profileScreen/shroomControl"
const val SHROOMBANK_SCREEN = "profileScreen/shroomBank"

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val mapViewModel: MapViewModel = hiltViewModel()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var onboardingCompleted by remember { mutableStateOf(false) }
    val currentRoute = currentBackStackEntry?.destination?.route
    var showStartPopup by remember { mutableStateOf(true) }


    Scaffold(
        bottomBar = {
            if(currentRoute!=ONBOARDING_SCREEN) {       //Hides the bottom bar when onboarding happens
                BottomNavigationBar(navController = navController)
            }}
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = if (onboardingCompleted) MAP_SCREEN else ONBOARDING_SCREEN,

            modifier = modifier
                .fillMaxSize()
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
            //UNDER-ITEMS for Profile-screen
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
                ShroomControlScreen()
            }
            composable(SHROOMBANK_SCREEN) {
                ShroomBankScreen(navController = navController)
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

            composable(MUSHROOM_LIST_SCREEN) { backStackEntry ->
                val viewModel: MushroomInfoViewModel = hiltViewModel(backStackEntry)
                MushroomListScreen(
                    navigateToMushroom = { navController.navigate(MUSHROOM_INFO_SCREEN) },
                    viewModel = viewModel
                )
            }

            composable(MUSHROOM_INFO_SCREEN) {
                MushroomInfoScreen(
                    onNavigateBack = {
                        navController.navigate(MUSHROOM_LIST_SCREEN) {
                            popUpTo(MUSHROOM_LIST_SCREEN) {
                                inclusive = false
                            }
                        }
                    }
                )
            }

            composable(MUSHROOM_INFO_SCREEN) { backStackEntry ->
                // Get the list screen's back stack entry to share its ViewModel
                val listEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(MUSHROOM_LIST_SCREEN)
                }
                val viewModel: MushroomInfoViewModel = hiltViewModel(listEntry)
                MushroomInfoScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }

        }
    }
}













