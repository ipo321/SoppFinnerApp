package no.uio.ifi.in2000.team16.ui.map

import android.Manifest
import no.uio.ifi.in2000.team16.R
import org.maplibre.android.style.layers.Property.NONE
import org.maplibre.android.style.layers.Property.VISIBLE
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.foundation.lazy.items
import android.util.Log
import android.view.Gravity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import no.uio.ifi.in2000.team16.domain.model.BoundingBox
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.expressions.Expression.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import kotlinx.coroutines.Job
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.SymbolLayer

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
    isVisible: Boolean,
    onMapLoaded: () -> Unit
) {
    val context = LocalContext.current
    //
    var locationPermissionGranted by remember {mutableStateOf(
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val queryText by remember { mutableStateOf("") }
    val currentJob = remember { mutableStateOf<Job?>(null) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val searchTextFieldState = rememberTextFieldState()
    // The map is made with MapView and remember to save state
    val mapView = getMapView(viewModel, context, onMapLoaded)
    // mapView is alerted when lifecycle changes
    LifeCycleObserver(mapView)

    LaunchedEffect(isVisible) {
        if (isVisible) {
            mapView.onResume()
        } else {
            mapView.onPause()
        }
    }


    //Shows the "Allow Location" popup and remembers the response
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }
        //Actually runs the popup and makes sure it doesn't rerun
    LaunchedEffect(Unit){
        locationPermissionRequest.launch(arrayOf(
             Manifest.permission.ACCESS_COARSE_LOCATION,
             Manifest.permission.ACCESS_FINE_LOCATION
        ))
    }

    //Sets up the GPS listner
    DisposableEffect(locationPermissionGranted){
        if(!locationPermissionGranted) return@DisposableEffect onDispose{}

        val locationManager = context.getSystemService(
            Context.LOCATION_SERVICE) as LocationManager

        //fires every time we want to update the user position from the GPS
        //pushes the coordinates to the viewmodel
        val listener = LocationListener { location ->
            viewModel.onUserLocationUpdated(location.latitude, location.longitude)
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            3000L, //for every 3 seconds
            10f, //if moved 10 meters
            listener
        )
        //disposes of the GPS listener(to avoid leaks)
        onDispose{locationManager.removeUpdates(listener)}
    }

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
        
        MapSearchBar(
            viewModel = viewModel,
            uiState = uiState,
            mapView = mapView,
            onNavigateToDev = onNavigateToDev,
            textFieldState = searchTextFieldState,
            onSearch = { },
        )

        when (uiState.screenStatus) {
            is ScreenStatus.Success -> {
                uiState.evaluation?.let { eval ->
                    InfoPopUp(
                        msg = eval.description,
                        score = eval.score,
                        contextData = uiState.currentContext,
                        onDismiss = { viewModel.dismissEvaluation() }
                    )
                }
            }
            is ScreenStatus.Loading -> InfoPopUp("Laster inn data...", score = 0,
                contextData = null, onDismiss = null)
            is ScreenStatus.Error -> InfoPopUp("Feil: ${(uiState.screenStatus as ScreenStatus.Error).message}",
                score = 0, contextData = null, onDismiss = null)
            is ScreenStatus.Idle -> {}
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchBar(
    viewModel: MapViewModel,
    uiState: MapScreenState,
    mapView: MapView,
    onNavigateToDev: () -> Unit,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = {
                        textFieldState.edit { replace(0, length, it) }
                        viewModel.onSearchQueryChanged(it) // Collects suggestions while typing
                        searchExpanded = true
                    },
                    onSearch = { query ->
                        viewModel.searchAddressByText(query) { lat, lon ->
                            mapView.getMapAsync { map ->
                                map.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 15.0)
                                )
                            }
                        }
                        onSearch(textFieldState.text.toString())
                        searchExpanded = false // closes when search is entered/completed
                    },
                    expanded = searchExpanded,
                    onExpandedChange = { searchExpanded = it },
                    placeholder = { Text("Søk i kartet") },
                    leadingIcon = { MapMenu(
                        viewModel = viewModel,
                        uiState = uiState,
                        mapView = mapView,
                        menuExpanded = menuExpanded,
                        onClick = { menuExpanded = !menuExpanded },
                        onDismissRequest = { menuExpanded = false },
                        onNavigateToDev = onNavigateToDev
                    ) },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                )
            },
            expanded = searchExpanded,
            onExpandedChange = { searchExpanded = it },
        ) {
            // Displays search results in a scrollable column
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(uiState.addressSuggestions) { suggestion ->
                    ListItem(
                        headlineContent = { Text("${suggestion.adressetekst} ${suggestion.poststed} ${suggestion.kommunenavn}") },
//                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { // each item is clickable
                                viewModel.searchAddress(suggestion) { lat, lon ->
                                    mapView.getMapAsync { map ->
                                        map.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(lat, lon), 15.0 // 15 is how close we want the zoom after searching
                                            )
                                        )
                                    }
                                    viewModel.onMapClicked(lat, lon)
                                }
                                // updates the searchbar text
                                val text = "${suggestion.adressetekst} ${suggestion.poststed} ${suggestion.kommunenavn}"
                                textFieldState.edit { replace(0, length, text) }
                                searchExpanded = false
                            }
                            //.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MapMenu(
    viewModel: MapViewModel,
    uiState: MapScreenState,
    mapView: MapView,
    menuExpanded: Boolean,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onNavigateToDev: () -> Unit,

) {
    Box {
        IconButton(onClick = onClick) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
        Dropdown(
            expanded = menuExpanded,
            onDismissRequest = onDismissRequest,
            onNavigateToDev = onNavigateToDev,
            isHeatmapEnabled = uiState.isHeatmapEnabled,
            isHistoricalFindsEnabled = uiState.isHistoricalFindsEnabled,
            onToggleHistoricalFinds = { viewModel.toggleHistoricalFinds() },
            favoriteMushrooms = uiState.favoriteMushrooms,
            selectedMushroom = uiState.selectedMushroom,
            onMushroomSelected = { mushroom ->
                viewModel.onMushroomSelected(
                    mushroom
                )
            },
            onToggleHeatMap = {
                mapView.getMapAsync { map ->
                    val bounds = map.projection.visibleRegion.latLngBounds
                    val currentBox = BoundingBox(
                        north = bounds.northEast.latitude,
                        south = bounds.southWest.latitude,
                        east = bounds.northEast.longitude,
                        west = bounds.southWest.longitude
                    )
                    viewModel.toggleHeatmap(currentBox)
                }
            }
        )
    }
}

@Composable
fun getMapView(
    viewModel: MapViewModel,
    context: Context,
    onMapLoaded: () -> Unit
): MapView {
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
                if(isDarkMode(context))
                    "https://api.maptiler.com/maps/019daf6d-d281-745e-939c-9621fc2be699/style.json?key=OXxwimrGO7F3CL0neXBq"
                else
                    "https://api.maptiler.com/maps/019dab82-a3ed-7f0e-8e50-339cb9610079/style.json?key=OXxwimrGO7F3CL0neXBq"

        currentMap.setStyle(styleUrl) { style ->
            Log.d("T16_MAP", "Style loaded: ${style.uri}")
            setupHeatmapLayer(style)
            setupHistoricalFindsLayer(style)
            setupForestLayer(style, uiState.showWmsForest)
            //setupPointMarker(style)
            addPinImage(style, context)
            setupPinIcon(style)
            setupUserLocationLayer(style)
            onMapLoaded()
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
    // Update pin where clicked with each new click
    LaunchedEffect(
        uiState.lastClickedLat,
        uiState.lastClickedLon,
        mapRef
    ) {
        val currentMap = mapRef ?: return@LaunchedEffect
        val lat = uiState.lastClickedLat ?: return@LaunchedEffect
        val lon = uiState.lastClickedLon ?: return@LaunchedEffect

        currentMap.getStyle { style ->
            val source = style.getSourceAs<GeoJsonSource>("clicked-source") ?: return@getStyle
            val point = Point.fromLngLat(lon, lat)
            source.setGeoJson(Feature.fromGeometry(point))
        }
    }
    //Drawing the users position dot
    LaunchedEffect(uiState.userLocation, mapRef){
        val currentMap = mapRef ?: return@LaunchedEffect
        val location = uiState.userLocation ?: return@LaunchedEffect
        currentMap.getStyle{ style ->
            val source = style.getSourceAs<GeoJsonSource>("user-location-source")
                ?: return@getStyle
            val point = Point.fromLngLat(location.longitude, location.latitude)
            source.setGeoJson(Feature.fromGeometry(point))
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
fun LifeCycleObserver(mapView: MapView) {

    val context = LocalContext.current
    val lifecycleOwner = context as? LifecycleOwner ?: LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
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
    onDismiss: (() -> Unit)?,
    contextData: EnvironmentContext?,
    modifier: Modifier = Modifier
) {
    ScoreCard(
        score = score,
        description = msg,
        contextData = contextData,
        onDismiss = onDismiss,
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
                stop(0, rgba(248, 113, 113, 0.4f)),     // red
                stop(15, rgba(251, 146, 60, 0.4f)),   // Orange
                stop(50, rgba(250, 204, 21, 0.4f)),// Yellow
                stop(100, rgba(74, 222, 128, 0.4f))  // Green
            )
        ),
        //Changes the border
        fillOutlineColor(rgba(0, 0, 0, 0f)),
        fillOpacity(1f)
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
        circleColor("#D0ECA8"),   // PrimaryFixed
        circleStrokeWidth(1.5f),
        circleStrokeColor("#1B1C15")  //OnSurfaceDark
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

private fun addPinImage(style: Style, context: Context) {
    val drawable = AppCompatResources.getDrawable(context, R.drawable.location_pin)
        ?: return

    val bitmap = drawable.toBitmap()  // Makes the drawable into a bitmap for MapLibre
    style.addImage("pin-icon", bitmap)
}

private fun setupPinIcon(style: Style) {
    val source = GeoJsonSource("clicked-source")
    style.addSource(source)

    val symbolLayer = SymbolLayer("clicked-layer", "clicked-source")
        .withProperties(
            iconImage("pin-icon"),
            iconSize(1.5f), // The size of the pin-icon
            iconAllowOverlap(true),
            iconIgnorePlacement(true),
            iconAnchor(Property.ICON_ANCHOR_BOTTOM) // Makes the pin-tip point where clicked
        )
    style.addLayer(symbolLayer)
}
private fun setupPointMarker(style: Style) {
    val source = GeoJsonSource("position-source")
    style.addSource(source)

    val circleLayer = CircleLayer("position-layer", "position-source")
    circleLayer.setProperties(
        circleRadius(6f),
        circleColor("#2769DB"),   // blue like Google Maps
        circleStrokeWidth(1.5f),
        circleStrokeColor("#FFFFFF")
    )
    style.addLayer(circleLayer)

}

//Sets up the userPosition on the map

private fun setupUserLocationLayer(style: Style){
    val source = GeoJsonSource("user-location-source")
    style.addSource(source)

    //Outer ring around user position (Pulsing ring)
    val pulseLayer = CircleLayer("user-location-pulse", "user-location-source")
    pulseLayer.setProperties(
        circleRadius(10f),
        circleColor("#2769DB"),
        circleStrokeWidth(2f),
        circleStrokeColor("#FFFFF")

    )
    style.addLayer(pulseLayer)

    val dotLayer = CircleLayer("user-location-layer", "user-location-source")
    dotLayer.setProperties(
        circleRadius(6f),
        circleColor("#2769DB"),
        circleStrokeWidth(1f),
        circleStrokeColor("#FFFFF")
    )
    style.addLayer(dotLayer)
}

fun isDarkMode(context: Context): Boolean {
    val darkModeFlag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
}