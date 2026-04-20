package no.uio.ifi.in2000.team16.ui.map

import org.maplibre.android.style.layers.Property.NONE
import org.maplibre.android.style.layers.Property.VISIBLE
import android.content.Context
import android.util.Log
import android.view.Gravity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import no.uio.ifi.in2000.team16.domain.model.BoundingBox
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.expressions.Expression.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.ui.popUp.DialogWithImage
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet

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
    onNavigateToDev: () -> Unit,
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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
        if (uiState.isFetchingHeatmap) {
            LinearProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top=25.dp,end=16.dp)
                .wrapContentSize()
        ) {
            Dropdown(
                onNavigateToDev = onNavigateToDev,
                isHeatmapEnabled = uiState.isHeatmapEnabled,
                isHistoricalFindsEnabled = uiState.isHistoricalFindsEnabled,
                onToggleHistoricalFinds = { viewModel.toggleHistoricalFinds() },
                favoriteMushrooms = uiState.favoriteMushrooms,
                selectedMushroom = uiState.selectedMushroom,
                onMushroomSelected = { mushroom -> viewModel.onMushroomSelected(mushroom) },
                onToggleHeatMap = {
                    mapView.getMapAsync { map ->
                        val bounds = map.projection.visibleRegion.latLngBounds
                        val north = bounds.northEast.latitude
                        val south = bounds.southWest.latitude
                        val east = bounds.northEast.longitude
                        val west = bounds.southWest.longitude

                        val latDiff = north - south
                        val lonDiff = east - west

                        val expandedBounds = BoundingBox(
                            north = north + (latDiff * 0.2),
                            south = south - (latDiff * 0.2),
                            east = east + (lonDiff * 0.4),
                            west = west - (lonDiff * 0.4)
                        )
                        viewModel.toggleHeatmap(expandedBounds)
                    }
                }
            )
        }

        when (uiState.screenStatus) {
            is ScreenStatus.Success -> {
                uiState.evaluation?.let { eval ->
                    InfoPopUp(
                        msg = eval.description,
                        score = eval.score,
                        contextData = uiState.currentContext
                    )
                }
            }
            is ScreenStatus.Loading -> InfoPopUp("Laster inn data...", score = 0, contextData = null)
            is ScreenStatus.Error -> InfoPopUp("Feil: ${(uiState.screenStatus as ScreenStatus.Error).message}", score = 0, contextData = null)
            is ScreenStatus.Idle -> {}
        }

    }
}

@Composable
fun getMapView(viewModel: MapViewModel, context: Context): MapView {

    val uiState by viewModel.uiState.collectAsState()
    var mapRef by remember { mutableStateOf<MapLibreMap?>(null) }


    val mapView = remember {
        MapView(context).apply {
            onCreate(null)

            getMapAsync { map ->
                mapRef = map
                setMapLayout(map)
                    map.addOnMapClickListener { coordinate ->
                        Log.d("MAP", "Map clicked at: $coordinate")

                        viewModel.onMapClicked(
                            lat = coordinate.latitude,
                            lon = coordinate.longitude
                        )
                        true
                    }
                    var lastTarget: LatLng? = null
                    var lastZoom: Double = 0.0

                    // Checks if map is still
                    map.addOnCameraIdleListener() {
                        val currentZoom = map.cameraPosition.zoom

                        if (!viewModel.uiState.value.isHeatmapEnabled || currentZoom < 10.0) return@addOnCameraIdleListener

                        val currentTarget = map.cameraPosition.target

                        if (currentTarget != null && lastTarget != null) {
                            val distanceInMeters = currentTarget.distanceTo(lastTarget!!)
                            val zoomDiff = kotlin.math.abs(currentZoom - lastZoom)

                            if (distanceInMeters < 200.0 && zoomDiff < 0.5) {
                                return@addOnCameraIdleListener
                            }
                        }

                        lastTarget = currentTarget
                        lastZoom = currentZoom

                        val visibleBounds = map.projection.visibleRegion.latLngBounds

                        val north = visibleBounds.northEast.latitude
                        val south = visibleBounds.southWest.latitude
                        val east = visibleBounds.northEast.longitude
                        val west = visibleBounds.southWest.longitude

                        val latDiff = north - south
                        val lonDiff = east - west

                        val expandedBounds = BoundingBox(
                            north = north + (latDiff * 0.2),
                            south = south - (latDiff * 0.2),
                            east = east + (lonDiff * 0.4),
                            west = west - (lonDiff * 0.4)
                        )

                        viewModel.fetchHeatmapData(expandedBounds)
                    }
                }
            }
        }
    LaunchedEffect(uiState.isSatelliteMode, mapRef) {
        val currentMap = mapRef ?: return@LaunchedEffect

        val styleUrl = if (uiState.isSatelliteMode)
                "https://api.maptiler.com/maps/019dabad-5643-770e-9f49-33576ba87164/style.json?key=OXxwimrGO7F3CL0neXBq"
            else
                "https://api.maptiler.com/maps/019dab82-a3ed-7f0e-8e50-339cb9610079/style.json?key=OXxwimrGO7F3CL0neXBq"

        currentMap.setStyle(styleUrl) { style ->
            Log.d("T16_MAP", "Style loaded: ${style.uri}")
            setupHeatmapLayer(style)
            setupHistoricalFindsLayer(style)
            setupForestLayer(style, uiState.showWmsForest)
        }
    }
    // WMS
    LaunchedEffect(uiState.showWmsForest, mapRef) {
        mapRef?.getStyle { style ->
            style.getLayer("satskog-layer")?.setProperties(
                visibility(if (uiState.showWmsForest) VISIBLE else NONE)
            )
        }
    }

    //Mushroom finding pins
    // Updating the pins each time they change
    LaunchedEffect(
        uiState.currentContext?.historicalFinds,
        uiState.isHistoricalFindsEnabled,
        mapRef
    ) {
        val currentMap = mapRef ?: return@LaunchedEffect
        val finds = uiState.currentContext?.historicalFinds ?: emptyList()



        val features = finds
            .filter { it.decimalLatitude != null && it.decimalLongitude != null }
            .map { occ ->
                Feature.fromGeometry(
                    Point.fromLngLat(occ.decimalLongitude!!, occ.decimalLatitude!!)
                )
            }

        currentMap.getStyle { style ->
            if (!style.isFullyLoaded) return@getStyle
            val locationSource = style.getSourceAs<GeoJsonSource>("historical-finds-source") ?: return@getStyle

            if (uiState.isHistoricalFindsEnabled) {
                locationSource.setGeoJson(FeatureCollection.fromFeatures(features))
            } else {
                locationSource.setGeoJson(FeatureCollection.fromFeatures(emptyList()))
            }
        }
    }

    // Updating the heatMapTiles each time they change
    LaunchedEffect(
        uiState.heatmapTiles,
        uiState.isHeatmapEnabled,
        mapRef) {
        val currentMap = mapRef ?: return@LaunchedEffect


        currentMap.getStyle { style ->
            if (!style.isFullyLoaded) return@getStyle

            val source = style.getSourceAs<GeoJsonSource>("heatmap-source") ?: return@getStyle

            if (uiState.isHeatmapEnabled && uiState.heatmapTiles.isNotEmpty()) {
                val features = mutableListOf<Feature>()

                val tilesToDraw = uiState.heatmapTiles.filter { tile ->
                    tile.size <= uiState.currentTileSize + 0.00001
                }

                for (tile in tilesToDraw) {
                    val halfSize = tile.size / 2.0

                    val polygonPoints = listOf(
                        Point.fromLngLat(tile.lon - halfSize, tile.lat - halfSize),
                        Point.fromLngLat(tile.lon + halfSize, tile.lat - halfSize),
                        Point.fromLngLat(tile.lon + halfSize, tile.lat + halfSize),
                        Point.fromLngLat(tile.lon - halfSize, tile.lat + halfSize),
                        Point.fromLngLat(tile.lon - halfSize, tile.lat - halfSize)
                    )

                    val polygon = org.maplibre.geojson.Polygon.fromLngLats(listOf(polygonPoints))
                    val feature = Feature.fromGeometry(polygon)

                    feature.addNumberProperty("score", tile.score)
                    features.add(feature)
                }

                val featureCollection = FeatureCollection.fromFeatures(features)
                source.setGeoJson(featureCollection)
                Log.d("T16_Map_Heat", "Drew ${features.size} grid polygons natively.")

            } else {
                // Clear the map safely
                source.setGeoJson(FeatureCollection.fromFeatures(emptyList()))
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
    msg : String,
    score : Int,
    contextData: EnvironmentContext?,
    modifier: Modifier = Modifier
) {
    ScoreCard(
        score = score,
        description = msg,
        contextData = contextData,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(16.dp)
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

private fun setupHeatmapLayer(style: Style) {
    //  Source
    val heatmapSource = GeoJsonSource("heatmap-source")
    style.addSource(heatmapSource)

    // Colour and Size
    val fillLayer = FillLayer("heatmap-layer", "heatmap-source")


    fillLayer.setProperties(
        fillColor(
            interpolate(
                linear(), get("score"),
                stop(0, rgba(200, 0, 0, 0.4f)),     // red
                stop(1, rgba(255, 100, 0, 0.5f)),   // Orange
                stop(50, rgba(255, 255, 0, 0.6f)),// Yellow
                stop(100, rgba(0, 255, 0, 0.7f))  // Green
            )
        ),
        //Changes the border
        fillOutlineColor(rgba(0, 0, 0, 0f)),
        fillOpacity(0.5f)
    )


    if (style.getLayer("Sand") != null) {
        style.addLayerBelow(fillLayer, "Sand")
    } else {
        style.addLayer(fillLayer)
    }
}

private fun setupHistoricalFindsLayer(style: Style) {
    val source = GeoJsonSource("historical-finds-source")
    style.addSource(source)

    val circleLayer = CircleLayer("historical-finds-layer", "historical-finds-source")
    circleLayer.setProperties(
        circleRadius(6f),
        circleColor("#8B4513"),   // brown pin color
        circleStrokeWidth(1.5f),
        circleStrokeColor("#FFFFFF")
    )
    style.addLayer(circleLayer)
}

private fun setupForestLayer(style: Style, isVisible: Boolean) {
    val satskogUrl = "https://wms.nibio.no/cgi-bin/satskog" +
            "?SERVICE=WMS" +
            "&VERSION=1.3.0" +
            "&REQUEST=GetMap" +
            "&LAYERS=Dominerende_treslag" +
            "&CRS=EPSG:3857" +
            "&BBOX={bbox-epsg-3857}" +
            "&WIDTH=256" +
            "&HEIGHT=256" +
            "&FORMAT=image/png" +
            "&TRANSPARENT=TRUE"

    val wmsSource = RasterSource(
        "satskog-source",
        TileSet("2.1.0", satskogUrl),
        256
    )
    style.addSource(wmsSource)
    val wmsLayer = RasterLayer("satskog-layer", "satskog-source")
    wmsLayer.setProperties(
        rasterOpacity(0.2f),
        visibility(if (isVisible) VISIBLE else NONE)
    )
    style.addLayer(wmsLayer)
}