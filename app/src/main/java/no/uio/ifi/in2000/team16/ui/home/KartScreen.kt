package no.uio.ifi.in2000.team16.ui.home

import android.annotation.SuppressLint
import android.content.Context
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
import no.uio.ifi.in2000.team16.data.layerList

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // The map is made with MapView and remember to save state
    val mapView = remember {
        MapView(context).apply {
            onCreate(null)

            getMapAsync { map ->
                // The map now uses BASE from MapTiler, this can be changed
                map.setStyle("https://api.maptiler.com/maps/base-v4/style.json?key=pzawSSamH6NaI2881UMa") { style ->
                    // Logging to see what happens when the map is used
                    Log.d("MAP", "Style loaded: ${style.uri}")

                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(59.91, 10.75)) // Where the map opens, now Oslo
                        .zoom(11.0) // Degree of zoom when map is opened
                        .build()
                    map.setMinZoomPreference(2.0) // Minimum zoom
                    map.setMaxZoomPreference(25.0) // Maximum zoom

                    val ui = map.uiSettings
                    // If we want a compass
                    ui.isCompassEnabled = true
                    ui.compassGravity = Gravity.TOP or Gravity.END
                    // Adds zoom, scroll, rotation, tilt and double tap
                    ui.isZoomGesturesEnabled = true
                    ui.isScrollGesturesEnabled = true
                    ui.isRotateGesturesEnabled = true
                    ui.isTiltGesturesEnabled = true
                    ui.isDoubleTapGesturesEnabled = true
                    ui.isHorizontalScrollGesturesEnabled = true

                    // The layerList is defined in GeoJSonLayer.kt
                    layerList().forEach { geoLayer ->
                        try {
                            val geoJson = context.assets.open(geoLayer.asset)
                                .bufferedReader().use { it.readText() }

                            // Makes the GeoJSon source
                            val source = GeoJsonSource(geoLayer.sourceId, geoJson)
                            style.addSource(source)

                            // Draws the polygons
                            val layer =
                                FillLayer(geoLayer.layerId, geoLayer.sourceId).withProperties(
                                    PropertyFactory.fillColor(geoLayer.color.toColorInt()),
                                    PropertyFactory.fillOpacity(0.5f), // Makes the layer a little transparent
                                    PropertyFactory.fillOutlineColor(Color.BLACK) // Black outline
                                )
                            style.addLayer(layer)
                        } catch (e: Exception) {
                            Log.e("MAP", "Error when loading ${geoLayer.asset}", e)
                        }
                    }
                }
            }
        }
    }
    // mapView is alerted when lifecycle changes
    DisposableEffect(lifecycleOwner, mapView) {
        // observator listens to the lifecycle and when each of the functions below
        // are called by the observator, the same ones are called on mapView
        val observator = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                mapView.onStart()
            }

            override fun onResume(owner: LifecycleOwner) {
                mapView.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                mapView.onPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                mapView.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                mapView.onDestroy()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observator)
        // When mapScreen is removed, so is the observator and mapView no longer calls the above functions
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observator)
        }
    }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView }
    )
}




/* WMS code no longer in use:

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