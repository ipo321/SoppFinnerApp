package no.uio.ifi.in2000.team16.ui.home

import android.util.Log
import android.view.Gravity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

@Composable
fun KartScreen() {
    val kontekst = LocalContext.current
    val livssyklusEier = LocalLifecycleOwner.current
    // Her opprettes kartet med MapView og remember slik at state huskes
    val kartView = remember {
        MapView(kontekst).apply {
            onCreate(null)

            getMapAsync { kart ->
                // Foreløpig bruk er kartet Base fra Maptiler, kan byttes ut
                kart.setStyle("https://api.maptiler.com/maps/base-v4/style.json?key=pzawSSamH6NaI2881UMa") { stil ->
                    // Logging for å se hva som skjer når kartet brukes
                    Log.d("MAP", "Stil lastet: ${stil.uri}")

                    kart.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(59.91, 10.75)) // Utsnittet hvor kartet åpnes. Nå Oslo.
                        .zoom(11.0) // Grad av zoom ved åpning av kart
                        .build()
                    kart.setMinZoomPreference(2.0) // Minimum zoom
                    kart.setMaxZoomPreference(25.0) // Maximum zoom

                    val ui = kart.uiSettings
                    // Hvis vi vil ha med kompass
                    ui.isCompassEnabled = true
                    ui.compassGravity = Gravity.TOP or Gravity.END
                    // Legger til zoom, scrolling, rotering, tilting og dobbelttrykk
                    ui.isZoomGesturesEnabled = true
                    ui.isScrollGesturesEnabled = true
                    ui.isRotateGesturesEnabled = true
                    ui.isTiltGesturesEnabled = true
                    ui.isDoubleTapGesturesEnabled = true
                    ui.isHorizontalScrollGesturesEnabled = true

                }
            }
        }
    }
    // kartViewet får beskjed når livssyklusen endres
    DisposableEffect(livssyklusEier, kartView) {
        // observator lytter til livssyklusen og når hver av funksjonene under kalles på
        // observatoren, så kalles de samme på kartView
        val observator = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                kartView.onStart()
            }
            override fun onResume(owner: LifecycleOwner) {
                kartView.onResume()
            }
            override fun onPause(owner: LifecycleOwner) {
                kartView.onPause()
            }
            override fun onStop(owner: LifecycleOwner) {
                kartView.onStop()
            }
            override fun onDestroy(owner: LifecycleOwner) {
                kartView.onDestroy()
            }
        }
        livssyklusEier.lifecycle.addObserver(observator)
        // Når MapScreen fjernes, fjernes observatoren og mapViewet kaller ikke lenger på funksjonene over
        onDispose {
            livssyklusEier.lifecycle.removeObserver(observator)
        }
    }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { kartView }
    )
}
