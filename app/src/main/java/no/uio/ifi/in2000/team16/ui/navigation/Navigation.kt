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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoScreen
import no.uio.ifi.in2000.team16.ui.dev.DevScreen
import no.uio.ifi.in2000.team16.ui.map.MapScreen
import no.uio.ifi.in2000.team16.ui.map.MapViewModel
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoViewModel
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomListScreen
import no.uio.ifi.in2000.team16.ui.minSide.HistoryScreen
import no.uio.ifi.in2000.team16.ui.minSide.InfoScreen
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreen
import no.uio.ifi.in2000.team16.ui.onboard.OnboardingScreen
import no.uio.ifi.in2000.team16.ui.onboard.WelcomeViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import no.uio.ifi.in2000.team16.ui.loading.LoadingScreen
import no.uio.ifi.in2000.team16.ui.minSide.SettingsScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomBankScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomControlScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomSpotScreen
import no.uio.ifi.in2000.team16.ui.popUp.DialogWithImage
import no.uio.ifi.in2000.team16.R

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
const val SHROOMCONTROL_SCREEN = "profileScreen/shroomControll"
const val SHROOMBANK_SCREEN = "profileScreen/shroomBank"

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val welcomeViewModel: WelcomeViewModel = hiltViewModel()
    val onboardingCompleted by welcomeViewModel.onBoardingCompleted.collectAsState()

    var isTimerFinished by remember { mutableStateOf(false) }


    //A slights delay to show the loading screen before the app starts
    //Not good code practice
    LaunchedEffect(Unit) {
        delay(2000) // 2 second delay
        isTimerFinished = true

    }


    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val mapViewModel: MapViewModel = hiltViewModel()
    val mapUiState by mapViewModel.uiState.collectAsState()

    // Determine if the app is ready to transition away from the loading screen
    val isAppReady = onboardingCompleted != null && isTimerFinished
    
    // Specifically check if we are going to the map and if the map is ready.
    // If onboarding is NOT completed, we don't need to wait for the map.
    val isMapReady = onboardingCompleted == false || mapUiState.selectedMushroom != null

    var showMapWarning by remember { mutableStateOf(false) }
    LaunchedEffect(currentRoute) {
        if (currentRoute == MAP_SCREEN) {
            showMapWarning = true
        }
    }

    if (!isAppReady || (onboardingCompleted == true && !isMapReady)) {
        LoadingScreen()
        return
    }

    // Display the dialog over the UI if showMapWarning is true
    if (showMapWarning) {
        DialogWithImage(
            onDismissRequest = { showMapWarning = false },
            onConfirmation = { showMapWarning = false },
            painter = painterResource(id = R.drawable.dead_sprite),
            imageDescription = "Warning image"
        )
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != ONBOARDING_SCREEN) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (onboardingCompleted == true) MAP_SCREEN else ONBOARDING_SCREEN,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(ONBOARDING_SCREEN) {
                OnboardingScreen(
                    onFinished = {
                        welcomeViewModel.saveOnBoardingState(completed = true)
                        navController.navigate(MAP_SCREEN) {
                            popUpTo(ONBOARDING_SCREEN) { inclusive = true }
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
                ShroomControlScreen(navController = navController)
            }
            composable(SHROOMBANK_SCREEN) {
                ShroomBankScreen(navController = navController)
            }

            composable(DEV_SCREEN) {

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
