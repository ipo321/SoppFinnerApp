package no.uio.ifi.in2000.team16.ui.home

import android.content.Context
import android.util.Log
import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import no.uio.ifi.in2000.team16.data.polygons.Municipalities
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource

/*
    MapLibre on GitHub: https://github.com/maplibre/maplibre-native
    Tutorials: https://maplibre.org/maplibre-native/android/examples/

    The map is partly configured in res/layout/main.xml

    The tiles that shows cities, streets etc. is from the map Base from MapTiler: https://cloud.maptiler.com/maps/

    In the emulator the map can be zoomed in on by double tapping on the screen,
    or zoomed in or out by holding Ctrl and drag the map.
 */

@Composable
fun MapScreen(
    onButtonClicked: () -> Unit,
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current
    //val viewModel: MapViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    // The map is made with MapView and remember to save state
    val mapView = getMapView(viewModel, context)
    // mapView is alerted when lifecycle changes
    LifeCycleObserver(lifecycleOwner, mapView)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = { mapView }
        )
        Button(
            onClick = onButtonClicked,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        ) {
            Text("Knapp")

        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .wrapContentSize()
        ) {
            Dropdown()
        }

        when (uiState) {
            is UiState.Success -> {
                val forestInfo = (uiState as UiState.Success<MapUiState>).data.areaResource
                InfoPopUp("Arealdekar: ${forestInfo.acres}\n" +
                        "Arealtype: ${forestInfo.areaType}\n" +
                        "Treslag: ${forestInfo.woodSpecies}\n" +
                        "Grunnforhold: ${forestInfo.groundConditions}\n" +
                        "Skogbonitet: ${forestInfo.forestQuality}"
                )
            }
            is UiState.Loading -> InfoPopUp("Laster inn")
            is UiState.Error -> InfoPopUp("Feilet innlasting")
            is UiState.Idle -> {}
        }

    }
}

@Composable
fun getMapView(viewModel: MapViewModel, context: Context): MapView {
    val mapView = remember {
        MapView(context).apply {
            onCreate(null)

            getMapAsync { map ->
                // The map now uses BASE from MapTiler, this can be changed
                map.setStyle("https://api.maptiler.com/maps/base-v4/style.json?key=pzawSSamH6NaI2881UMa") { style ->
                    // Logging to see what happens when the map is used
                    Log.d("MAP", "Style loaded: ${style.uri}")

                    setMapLayout(map)
                    //addPolygonsToMap(context, style)  // No polygons for now

                    // Makes the map clickable
                    map.addOnMapClickListener { coordinate ->
                        Log.d("MAP", "Map clicked at: $coordinate")
                        val point = map.projection.toScreenLocation(coordinate)
                        val pxX = point.x.toInt() // Pixel position
                        val pxY = point.y.toInt()
                        val bounds = map.projection.visibleRegion.latLngBounds
                        val south = bounds.southWest.latitude
                        val west  = bounds.southWest.longitude
                        val north = bounds.northEast.latitude
                        val east  = bounds.northEast.longitude
                        val width  = this@apply.width // this@apply to get the MapView object from
                        val height = this@apply.height // the apply function above

                        viewModel.getForestInfo(
                            pxX, pxY, south, west, north, east, width, height
                        )

                        true // lambda returns true to inform that the click is managed
                    }
                }
            }
        }
    }
    return mapView
}

@Composable
fun LifeCycleObserver(lifecycleOwner: LifecycleOwner, mapView: MapView) {
    DisposableEffect(lifecycleOwner, mapView) {
        // observer listens to the lifecycle and when each of the functions below
        // are called by the observer, the same ones are called by mapView
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) { mapView.onStart() }
            override fun onResume(owner: LifecycleOwner) { mapView.onResume() }
            override fun onPause(owner: LifecycleOwner) { mapView.onPause() }
            override fun onStop(owner: LifecycleOwner) { mapView.onStop() }
            override fun onDestroy(owner: LifecycleOwner) { mapView.onDestroy() }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        // When mapScreen is removed, so is the observer and mapView no longer calls the above functions
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

// For now, the forest info is visible on screen
// In the future, this info will be sent to the score algorithm instead
@Composable
fun BoxScope.InfoPopUp(
    msg: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = msg,
        modifier = modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.8f))
            .padding(8.dp)
    )
}

fun setMapLayout(map: MapLibreMap) {
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
}


// This function is currently not in use, but might come in handy if we want polygons later on
fun addPolygonsToMap(context: Context, style: Style) {
    // The layerList is defined in GeoJSonLayer.kt
    Municipalities.municipalities.forEach { geoLayer ->
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
                    PropertyFactory.fillOutlineColor(android.graphics.Color.BLACK) // Black outline
                )
            style.addLayer(layer)
        } catch (e: Exception) {
            Log.e("MAP", "Error when loading ${geoLayer.asset}", e)
        }
    }
}