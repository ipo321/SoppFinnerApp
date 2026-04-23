package no.uio.ifi.in2000.team16

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team16.ui.navigation.Navigation
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme
import org.maplibre.android.MapLibre

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        MapLibre.getInstance(this)

        enableEdgeToEdge()

        setContent {
            Team16Theme {
                Navigation()
            }
        }
    }
}


