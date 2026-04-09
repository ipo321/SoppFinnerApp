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
import androidx.compose.runtime.remember
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoScreen
import no.uio.ifi.in2000.team16.ui.dev.DevScreen
import no.uio.ifi.in2000.team16.ui.dev.DevViewModel
import no.uio.ifi.in2000.team16.ui.map.MapScreen
import no.uio.ifi.in2000.team16.ui.map.MapViewModel
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoViewModel
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomListScreen


//
const val MAP_SCREEN = "map"
const val DEV_SCREEN = "dev"

const val MUSHROOM_INFO_SCREEN = "mushroomInfo"

const val MUSHROOM_LIST_SCREEN = "mushroomList"

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
            composable(Screen.MinSide.rout) {
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

            composable(MUSHROOM_LIST_SCREEN) { backStackEntry ->
                val viewModel: MushroomInfoViewModel = hiltViewModel(backStackEntry)
                MushroomListScreen(
                    onMushroomClick = { navController.navigate(MUSHROOM_INFO_SCREEN) },
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

















