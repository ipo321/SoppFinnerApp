package no.uio.ifi.in2000.team16.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoScreen


//
const val MAP_SCREEN = "map"
const val MODULE_SCREEN = "module"

const val MUSHROOM_INFO_SCREEN = "mushroomInfo"



@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val mapViewModel: MapViewModel = hiltViewModel()
    val moduleViewModel: ModuleViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = MAP_SCREEN,
        modifier = modifier.fillMaxSize()
    ) {
        composable(MAP_SCREEN) {
            MapScreen(
                onButtonClicked = {
                    navController.navigate(MODULE_SCREEN)
                },
                viewModel = mapViewModel
            )
        }
        composable(MODULE_SCREEN){
            ModuleScreen(
                onNavigateBack = {
                    navController.navigate(MAP_SCREEN)
                },
                viewModel = moduleViewModel
            )
        }

        composable(MUSHROOM_INFO_SCREEN){
            MushroomInfoScreen(
                onNavigateHome = {
                    navController.navigate(MAP_SCREEN){
                        popUpTo(MAP_SCREEN){
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}














