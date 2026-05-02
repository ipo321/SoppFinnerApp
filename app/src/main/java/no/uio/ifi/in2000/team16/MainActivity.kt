package no.uio.ifi.in2000.team16

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team16.ui.navigation.Navigation
import no.uio.ifi.in2000.team16.ui.snackbar.observeConnectivity
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme
import org.maplibre.android.MapLibre
import androidx.compose.material3.Snackbar
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team16.ui.snackbar.observeLocationAvailability


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        MapLibre.getInstance(this)

        enableEdgeToEdge()

        setContent {
            val systemTheme = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(systemTheme) }
                Team16Theme (
                    darkTheme = darkTheme

                ){
                    val navController = rememberNavController()
                    val context = LocalContext.current

                    //Starts the internet connection check as if it has an internet connection
                    val isNetConnected by observeConnectivity(context)
                        .collectAsState(initial = true)

                    //Starts the location connection check as if it has the location connection
                    val isPosConnected by observeLocationAvailability(context)
                        .collectAsState(initial = true)

                    val internetSnackbarState = remember { SnackbarHostState() }
                    val locationSnackbarState = remember { SnackbarHostState() }

                    //LaunchEffect for snackBar that shows No internet
                    LaunchedEffect(isNetConnected) {
                        if (!isNetConnected) {
                            internetSnackbarState.showSnackbar(
                                message = "Ingen internettforbindelse..",
                                duration = SnackbarDuration.Indefinite
                            )
                        } else {
                            internetSnackbarState.currentSnackbarData?.dismiss()
                        }
                    }

                    //LaunchEffect for snackBar that shows No internet
                    LaunchedEffect(isPosConnected) {
                        if (!isPosConnected) {
                            locationSnackbarState.showSnackbar(
                                message = "Finner ikke posisjonen din..",
                                duration = SnackbarDuration.Indefinite
                            )
                        } else {
                            locationSnackbarState.currentSnackbarData?.dismiss()
                        }
                    }

                    Box {


                        Navigation(
                            modifier = Modifier,
                            navController = navController,
                            darkTheme = darkTheme,
                            onToggleTheme = { darkTheme = !darkTheme }
                        )

                        //Internet
                        SnackbarHost(
                            hostState = internetSnackbarState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                                .padding(bottom = 95.dp),
                        ) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }

                        //Location
                        SnackbarHost(
                            hostState = locationSnackbarState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                                .padding(bottom = 150.dp),
                        ) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }


