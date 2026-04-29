package no.uio.ifi.in2000.team16.ui.navigation

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoScreen
import no.uio.ifi.in2000.team16.ui.dev.DevScreen
import no.uio.ifi.in2000.team16.ui.map.MapScreen
import no.uio.ifi.in2000.team16.ui.map.MapViewModel
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoViewModel
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomListScreen
import no.uio.ifi.in2000.team16.ui.minSide.HistoryScreen
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreen
import no.uio.ifi.in2000.team16.ui.onboard.OnboardingScreen
import no.uio.ifi.in2000.team16.ui.onboard.WelcomeViewModel
import no.uio.ifi.in2000.team16.ui.loading.LoadingScreen
import no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions.SettingsScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomBankScreen
import no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions.ShroomControlScreen
import no.uio.ifi.in2000.team16.ui.minSide.ShroomSpotScreen
import no.uio.ifi.in2000.team16.ui.popUp.DialogWithImage
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.ui.dev.DevViewModel
import no.uio.ifi.in2000.team16.ui.minSide.InfoScreen
import no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions.PoisonControl
import no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions.Security
import no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions.ScoreInfo

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
const val INFO_SCREEN = "InfoScreen"
const val SHROOMCONTROL_SCREEN = "profileScreen/shroomControl"
const val SHROOMBANK_SCREEN = "profileScreen/shroomBank"

const val SECURITY_SCREEN = "Security"

const val SCORE_INFO_SCREEN = "ScoreInfo"

const val POISON_CONTROLL = "PoisonControll"


@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    darkTheme: Boolean,
    onToggleTheme: () -> Unit
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
    val context = LocalContext.current

    var isMapLoaded by remember { mutableStateOf(false) }
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val mapViewModel: MapViewModel = hiltViewModel()
    val mushroomInfoViewModel: MushroomInfoViewModel = hiltViewModel()
    val mapUiState by mapViewModel.uiState.collectAsState()

    // Determine if the app is ready to transition away from the loading screen
    val isAppReady = onboardingCompleted != null && isTimerFinished

    // Specifically check if we are going to the map and if the map is ready.
    val isMapReady = onboardingCompleted == false || mapUiState.selectedMushroom != null

    // State to control the visibility of the warning dialog
    var showMapWarning by remember { mutableStateOf(false) }

    LaunchedEffect(isAppReady) {
        if (isAppReady) {
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
            if(currentRoute!=ONBOARDING_SCREEN) {       //Hides the bottom bar when onboarding happens
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding/*top = innerPadding.calculateTopPadding()*/)
        ) {
            val isMapVisible = (currentRoute == MAP_SCREEN)

            if (onboardingCompleted == true && currentRoute != ONBOARDING_SCREEN) {
                MapScreen(
                    onNavigateToDev = { navController.navigate(DEV_SCREEN) },
                    mapViewModel = mapViewModel,
                    isVisible = isMapVisible,
                    onMapLoaded = { isMapLoaded = true }
                )
            }
            NavHost(
                navController = navController,
                startDestination = if (onboardingCompleted == true) MAP_SCREEN else ONBOARDING_SCREEN,
                modifier = modifier
                    .fillMaxSize()
                    //.padding(if (isMapVisible) PaddingValues(0.dp) else innerPadding)
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
            }

            composable(PROFILE_SCREEN) {
                ProfileScreen(
                    navController = navController,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    darkTheme = darkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
            //UNDER-ITEMS for Profile-screen
            composable(SETTINGS_SCREEN) {
                SettingsScreen(
                    navController = navController,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    darkTheme = darkTheme,
                    onToggleTheme = onToggleTheme,
                )
            }
            composable(HISTORY_SCREEN) {
                HistoryScreen(navController = navController)
            }
            composable(SHROOMSPOT_SCREEN) {
                ShroomSpotScreen(navController = navController)
            }

            composable(SHROOMCONTROL_SCREEN) {
                ShroomControlScreen(navController = navController,
                    onNavigateBack = {
                        navController.popBackStack()
                    })
            }
            composable(SHROOMBANK_SCREEN) {
                ShroomBankScreen(navController = navController)
            }
                composable(INFO_SCREEN) {
                    InfoScreen(navController=navController,
                        onNavigateBack = {
                            navController.popBackStack()
                        })
                }

                composable (SCORE_INFO_SCREEN ){
                    ScoreInfo(navController=navController,
                        onNavigateBack = {
                            navController.popBackStack()
                        })
                }
                composable(SECURITY_SCREEN){
                    Security(navController=navController,
                        onNavigateBack = {
                            navController.popBackStack()
                        })
                }
                composable (POISON_CONTROLL){
                    PoisonControl(navController=navController,
                        onNavigateBack = {
                            navController.popBackStack()
                        })
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
                    //val viewModel: MushroomInfoViewModel = hiltViewModel(backStackEntry)
                    MushroomListScreen(
                        navigateToMushroom = { navController.navigate(MUSHROOM_INFO_SCREEN) },
                        onMushroomControl = { navController.navigate(SHROOMCONTROL_SCREEN) },
                        viewModel = mushroomInfoViewModel
                    )
                }

                composable(MUSHROOM_INFO_SCREEN) { backStackEntry ->
                    // Get the list screen's back stack entry to share its ViewModel
                    val listEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(MUSHROOM_LIST_SCREEN)
                    }
                    //val viewModel: MushroomInfoViewModel = hiltViewModel(listEntry)
                    MushroomInfoScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onMushroomControl = { navController.navigate(SHROOMCONTROL_SCREEN) },
                        onNavigateToMap = { navController.navigate(MAP_SCREEN) },
                        viewModel = mushroomInfoViewModel
                    )
                }

            }

//            if (onboardingCompleted == true && !isMapLoaded && currentRoute != ONBOARDING_SCREEN) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(MaterialTheme.colorScheme.background),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Image(
//                            painter = painterResource(id = R.drawable.cute_mushroom),
//                            contentDescription = "Laster kart",
//                            modifier = Modifier.size(120.dp)
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            "Laster inn kartdata...",
//                            color = MaterialTheme.colorScheme.onBackground
//                        )
//                    }
//                }
//            }
        }
    }
}













