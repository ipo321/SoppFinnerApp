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
import org.maplibre.android.maps.MapLibreMapOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoViewModel
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.geojson.Polygon
import kotlin.math.abs

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
    mapViewModel: MapViewModel,
    isVisible: Boolean,
    onMapLoaded: () -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var locationPermissionGranted by remember {mutableStateOf(
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val uiState by mapViewModel.uiState.collectAsState()
    val searchTextFieldState = rememberTextFieldState()
    // The map is made with MapView and remember to save state
    val mapView = getMapView(mapViewModel, context, lifecycle, onMapLoaded)
    // mapView is alerted when lifecycle changes
    MapLifecycle(mapView, isVisible)

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
            mapViewModel.onUserLocationUpdated(location.latitude, location.longitude)
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
        MapComponents(
            viewModel = mapViewModel,
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
                        onDismiss = { mapViewModel.dismissEvaluation() }
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
fun MapComponents(
    viewModel: MapViewModel,
    uiState: MapScreenState,
    mapView: MapView,
    onNavigateToDev: () -> Unit,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    Column() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Team16Theme.padding.outer),
            verticalAlignment = Alignment.Top,
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    top = Team16Theme.padding.outer,
                    start = Team16Theme.padding.outer,
                    end = Team16Theme.padding.outer
                )
                .semantics { isTraversalGroup = true }
        ) {
            MapMenu(
                viewModel = viewModel,
                uiState = uiState,
                mapView = mapView,
                menuExpanded = menuExpanded,
                onClick = {
                    menuExpanded = !menuExpanded
                    viewModel.setSearchBarExpanded(false)
                },
                onDismissRequest = { menuExpanded = false },
                onNavigateToDev = onNavigateToDev
            )
            MapSearchBar(
                viewModel = viewModel,
                uiState = uiState,
                searchExpanded = uiState.isSearchBarExpanded,
                onExpandedChange = { expanded ->
                    viewModel.setSearchBarExpanded(expanded)
                   if (expanded) {
                       viewModel.dismissEvaluation()
                   }
                },
                mapView = mapView,
                textFieldState = textFieldState,
                onSearch = onSearch,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top,
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    top = Team16Theme.padding.outer,
                    end = Team16Theme.padding.outer
                )
        ) {
            MushroomIcon(
                mushroom = uiState.selectedMushroom
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(end = Team16Theme.padding.outer)
        ) {
            CentralizeMap(
                onClick = {
                    val userLocation = uiState.userLocation
                    if (userLocation != null) {
                        mapView.getMapAsync { map ->
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(userLocation.latitude, userLocation.longitude),
                                    15.0
                                )
                            )
                        }
                    }
                    viewModel.setSearchBarExpanded(false)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchBar(
    viewModel: MapViewModel,
    uiState: MapScreenState,
    searchExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    mapView: MapView,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DockedSearchBar(
        modifier = modifier
            .fillMaxWidth()
            .semantics { traversalIndex = 0f },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
        shape = if (searchExpanded) RoundedCornerShape(8.dp) else CircleShape,
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.fillMaxWidth(),
                query = textFieldState.text.toString(),
                onQueryChange = {
                    textFieldState.edit { replace(0, length, it) }
                    viewModel.onSearchQueryChanged(it) // Collects suggestions while typing
                    onExpandedChange(it.isNotEmpty())
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
                    onExpandedChange(false) // closes when search is entered/completed
                },
                expanded = searchExpanded,
                onExpandedChange = onExpandedChange,
                placeholder = { Text("Søk i kartet") },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textFieldState.edit { replace(0, length, "") }
                                viewModel.onSearchQueryChanged("")
                                //onExpandedChange(false)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tøm søk",
                                tint = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                    }
                    else {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                )
            )
        },
        expanded = searchExpanded,
        onExpandedChange = onExpandedChange,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        // Displays search results in a scrollable column
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp) // Max height of search result list
        ) {
            items(uiState.searchSuggestions) { suggestion ->
                ListItem(
                    headlineContent = { Text(suggestion.title) },
                    supportingContent = { Text(suggestion.subtitle) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { // each item is clickable
                            viewModel.searchLocation(suggestion) { lat, lon ->
                                mapView.getMapAsync { map ->
                                    map.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(lat, lon),
                                            13.0 // 13 is how close we want the zoom after searching
                                        )
                                    )
                                }
                            }
                            // updates the searchbar text
                            textFieldState.edit { replace(0, length, suggestion.title) }
                            onExpandedChange(false)
                        }
                )
                HorizontalDivider()
            }
        }
    }
}


@Composable
fun MushroomIcon(
    mushroom: Mushroom?,
    modifier: Modifier = Modifier
) {
    val selected = mushroom ?: return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            )
            .padding(Team16Theme.padding.inner)
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(Team16Theme.padding.inner)
        ) {
            Text(
                text = "Viser resultater for:",
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = selected.commonName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Column() {
            Box(
                modifier = Modifier
                    .size(Team16Theme.padding.mapButtonsSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(id = selected.imageRes),
                    contentDescription = selected.commonName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun CentralizeMap(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(top = Team16Theme.padding.outer)
            .size(Team16Theme.padding.mapButtonsSize)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Sentraliser kartet",
                tint = MaterialTheme.colorScheme.onSurface
            )
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Meny",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Dropdown(
            expanded = menuExpanded,
            onDismissRequest = onDismissRequest,
            onNavigateToDev = onNavigateToDev,
            isHeatmapEnabled = uiState.isHeatmapEnabled,
            isHistoricalFindsEnabled = uiState.isHistoricalFindsEnabled,
            onToggleHistoricalFinds = { viewModel.toggleHistoricalFinds() },
            isSatelliteMode = uiState.isSatelliteMode,
            onToggleSatellite = { viewModel.toggleSatellite() },
            showWmsForest = uiState.showWmsForest,
            onToggleWmsForest = { viewModel.toggleWmsForest() },
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
    lifeCycle: Lifecycle,
    onMapLoaded: () -> Unit
): MapView {
    val uiState by viewModel.uiState.collectAsState()
    var mapRef by remember { mutableStateOf<MapLibreMap?>(null) }


    val mapView = remember {
        // This line fixes the crash in dark mode bug, because it makes Map Libre use a Texture View
//        val mode = MapLibreMapOptions.createFromAttributes(context, null)
//            .textureMode(true)

        MapView(context, /*mode*/).apply { // mode are sent as argument here to fix the bug
            onCreate(null)

            getMapAsync { map ->
                mapRef = map
                setMapLayout(map, context)
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
                        val zoomDiff = abs(currentZoom - lastZoom)

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

                    val polygon = Polygon.fromLngLats(listOf(polygonPoints))
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

    //For making the screen start on the user position
    LaunchedEffect(uiState.moveCameraToUser, mapRef) {
        if(!uiState.moveCameraToUser) return@LaunchedEffect
        val currentMap = mapRef ?: return@LaunchedEffect
        val location = uiState.userLocation ?: return@LaunchedEffect

        currentMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(location, 13.0)
        )
        //resets the flag
        viewModel.onCameraMoved()
    }

    return mapView
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












