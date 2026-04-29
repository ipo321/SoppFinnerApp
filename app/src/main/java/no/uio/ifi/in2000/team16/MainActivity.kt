package no.uio.ifi.in2000.team16

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        MapLibre.getInstance(this)

        enableEdgeToEdge()

        setContent {
            Team16Theme {
                val navController= rememberNavController()
                val context = LocalContext.current

                val isConnected by observeConnectivity(context)
                    .collectAsState(initial = true)
                val snackbarHostState = remember {SnackbarHostState()}

                LaunchedEffect(isConnected) {
                    if(!isConnected){
                        snackbarHostState.showSnackbar(
                            message = "Ingen internettforbindelse..",
                            duration = SnackbarDuration.Indefinite
                        )
                    }else{snackbarHostState.currentSnackbarData?.dismiss()}
                }

                Box{
                    Navigation(
                        modifier = Modifier,
                        navController = navController,
                    )

                    SnackbarHost(hostState = snackbarHostState,
                            modifier = Modifier.align(Alignment.BottomCenter),
                    ){
                        data->
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
