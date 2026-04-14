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
import no.uio.ifi.in2000.team16.ui.onboard.OnboardingScreen


//
const val MAP_SCREEN = "map"
const val DEV_SCREEN = "dev"

const val MUSHROOM_INFO_SCREEN = "mushroomInfo"

const val ONBOARDING_SCREEN = "onboarding"



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
            startDestination = ONBOARDING_SCREEN,  // egt MAP_SCREEN dette er kun test
            modifier = modifier.fillMaxSize()
                .padding(innerPadding)
        ){
        composable(ONBOARDING_SCREEN) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(MAP_SCREEN) {
                        popUpTo(ONBOARDING_SCREEN) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        /*{
            composable(ONBOARDING_SCREEN) {
                MapScreen(
                    onNavigateToDev = {
                        navController.navigate(DEV_SCREEN)
                    },
                    viewModel = mapViewModel,
                )
            }

         */
            composable(Screen.MinSide.route) {
                MinSide()
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

















