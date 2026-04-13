package no.uio.ifi.in2000.team16.ui.navigation

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
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoScreen
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


//
const val MAP_SCREEN = "map"
const val DEV_SCREEN = "dev"

const val MUSHROOM_INFO_SCREEN = "mushroomInfo"

const val PROFILE_SCREEN = "profileScreen"
//Under the profile_screen menu:
const val SETTINGS_SCREEN = "profileScreen/settings"
const val HISTORY_SCREEN = "profileScreen/history"
const val SHROOMSPOT_SCREEN = "profileScreen/shroomSpots"
const val INFO_SCREEN = "profileScreen/info"
const val SHROOMCONTROL_SCREEN = "profileScreen/shroomControll"
const val SHROOMBANK_SCREEN = "profileScreen/shroomBank"



@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val mapViewModel: MapViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MAP_SCREEN,
            modifier = modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
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
                ShroomControlScreen(navController = navController)
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

















