package no.uio.ifi.in2000.team16

import MainScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team16.ui.home.KartScreen
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme
import org.maplibre.android.MapLibre

/*
    Bruker MapLibre Native som oppdateres jevnlig på github: https://github.com/maplibre/maplibre-native
    Tutorial finnes her: https://maplibre.org/maplibre-native/android/examples/

    Kartet er delvis konfigurert i res/layout/main.xml

    Selve tilsene som viser byer og gater osv er kartet Base fra https://cloud.maptiler.com/maps/

    I emulatoren kan kartet zoomes inn på ved å dobbelt-trykke på skjermen, eller zoome inn og ut
    ved å holde inne Ctrl og dra i kartet.
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        MapLibre.getInstance(this)

        enableEdgeToEdge()

        setContent {
            Team16Theme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    KartScreen()

                }
                // Kall på HomeScreen for å teste API-modul
                // HomeScreen(modulViewModel = hiltViewModel<ModulViewModel>())
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    Team16Theme {
        MainScreen()
    }
}