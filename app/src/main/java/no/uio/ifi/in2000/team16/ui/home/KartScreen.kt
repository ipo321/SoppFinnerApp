package no.uio.ifi.in2000.team16.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import androidx.core.graphics.toColorInt

//@SuppressLint("SetJavaScriptEnabled") // This turns of the warning on Java Script Enabled
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

                    try {
                        val geoJson = kontekst.assets.open("verdiklasser_ar5_oslo.geojson")
                            .bufferedReader().use { it.readText() }

                        // Makes the GeoJSon source
                        val source = GeoJsonSource("verdiklasser_ar5_oslo-source", geoJson)
                        stil.addSource(source)

                        // Draws the polygons
                        val layer = FillLayer(
                            "verdiklasser_ar5_oslo-layer",
                            "verdiklasser_ar5_oslo-source"
                        ).withProperties(
                            PropertyFactory.fillColor("#33AA33".toColorInt()), // Green fill
                            PropertyFactory.fillOpacity(0.5f), // Makes the layer a little transparent
                            PropertyFactory.fillOutlineColor(Color.BLACK) // Black outline
                        )
                        stil.addLayer(layer)
                    } catch (e: Exception) {
                        Log.e("MAP", "Error when loading GeoJSon")
                    }
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

    /*
    var showWms by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { kartView }
        )

        if (showWms) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        // Warning with enabling JavaScript, but safe as long as we do not give the
                        // user the opportunity to write their own urls.
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        webViewClient = WebViewClient()
                        loadUrl("file:///android_asset/map.html")
                    }
                }
            )
        }
    }
    */
}