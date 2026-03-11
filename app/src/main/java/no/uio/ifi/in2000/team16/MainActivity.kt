package no.uio.ifi.in2000.team16

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team16.ui.home.KartScreen
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import androidx.compose.ui.platform.LocalContext

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

        MapLibre.getInstance(this)

        enableEdgeToEdge()
        setContent {
            Team16Theme {
                // Kall på HomeScreen for å teste API-modul
                // HomeScreen(modulViewModel = hiltViewModel<ModulViewModel>())
                KartScreen()
            }
        }
    }
}