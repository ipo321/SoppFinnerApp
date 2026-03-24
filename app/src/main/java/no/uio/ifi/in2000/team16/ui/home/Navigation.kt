package no.uio.ifi.in2000.team16.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
object MapScreenRoute

// This will be changed when we have other screens
@Serializable
object ModuleScreenRoute

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val mapViewModel: MapViewModel = hiltViewModel<MapViewModel>()
    val moduleViewModel: ModuleViewModel = hiltViewModel<ModuleViewModel>()

    NavHost(
        navController = navController,
        startDestination = MapScreenRoute,
        modifier = modifier.fillMaxSize()
    ) {
        composable<MapScreenRoute> {
            MapScreen(
                onButtonClicked = {
                    navController.navigate(route = ModuleScreenRoute)
                },
                viewModel = mapViewModel
            )
        }
        composable<ModuleScreenRoute> {
            ModuleScreen(
                onNavigateBack = {
                    navController.navigate(route = MapScreenRoute)
                },
                viewModel = moduleViewModel
            )
        }
    }
}

















