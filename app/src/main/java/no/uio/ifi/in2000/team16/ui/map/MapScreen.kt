package no.uio.ifi.in2000.team16.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.BoundingBox
import no.uio.ifi.in2000.team16.ui.common.ErrorScreen
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.Property.NONE
import org.maplibre.android.style.layers.Property.VISIBLE
import org.maplibre.android.style.layers.PropertyFactory.visibility
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon

/*
    The Map Screen is the main screen in the app.

    MapLibre on GitHub: https://github.com/maplibre/maplibre-native

    In the emulator the map can be zoomed in on by double tapping on the screen,
    or zoomed in or out by holding Ctrl and drag the map.
 */

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    isVisible: Boolean,
    onMapLoaded: () -> Unit,
    isNetConnected: Boolean,
    isDarkMode: Boolean
) {
    val context = LocalContext.current
    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    //Shows the "Allow Location" popup and remembers the response
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }
    val uiState by mapViewModel.uiState.collectAsState()
    var mapRef by remember { mutableStateOf<MapLibreMap?>(null) }
    val searchTextFieldState = rememberTextFieldState()
    // The map is made with MapView and remember to save state
    val mapView = getMapView(mapViewModel, context) { mapRef = it }

    //Reload Map on internet reconect
    var hadDisconnection by remember { mutableStateOf(false) }

    LaunchedEffect(isNetConnected) {
        Log.d(
            "T16_Net", "isNetConnected changed: $isNetConnected " +
                    "hadDisconnection changed: $hadDisconnection"
        )
        if (!isNetConnected) {
            hadDisconnection = true
        } else if (hadDisconnection) {
            hadDisconnection = false
            mapViewModel.retryLastClick()
        }
    }


    LaunchEffects(
        viewModel = mapViewModel,
        uiState = uiState,
        mapRef = mapRef,
        context = context,
        locationPermissionRequest = locationPermissionRequest,
        locationPermissionGranted = locationPermissionGranted,
        onMapLoaded = onMapLoaded,
        isDarkMode = isDarkMode
    )
    // mapView is alerted when lifecycle changes
    MapLifecycle(mapView, isVisible)
    // Sets up the GPS listener
    DisposableEffect(locationPermissionGranted) {
        if (!locationPermissionGranted) return@DisposableEffect onDispose {}

        val locationManager = context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

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
        onDispose { locationManager.removeUpdates(listener) }
    }
    MapScreenSurface(
        searchTextFieldState = searchTextFieldState,
        mapView = mapView,
        mapViewModel = mapViewModel,
        uiState = uiState
    )
}

@Composable
fun MapScreenSurface(
    searchTextFieldState: TextFieldState,
    mapView: MapView,
    mapViewModel: MapViewModel,
    uiState: MapScreenState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { parentBounds ->
                val parentHeightPx = parentBounds.size.height

                mapView.post {
                    // post runs after layout calculations are done
                    // Makes sure the layout parameters are the same as the parent's.
                    // This fixes the recurring bug we have with the gap between map and nav-bar
                    val lp = mapView.layoutParams
                    if (lp != null && lp.height != parentHeightPx) {
                        lp.height = parentHeightPx
                        mapView.layoutParams = lp
                        mapView.requestLayout()
                    }
                }
            }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(), //matchParentSize(),
            factory = { mapView },
            update = { view ->
                val lp = view.layoutParams
                if (lp == null ||
                    lp.width != ViewGroup.LayoutParams.MATCH_PARENT ||
                    lp.height != ViewGroup.LayoutParams.MATCH_PARENT
                ) {
                    view.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
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
            textFieldState = searchTextFieldState,
            onSearch = { }
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

            is ScreenStatus.Loading -> InfoPopUp(
                msg = stringResource(R.string.laster_inn_data),
                score = 0,
                contextData = null,
                onDismiss = null
            )

            is ScreenStatus.Error -> ErrorScreen("Feil: ${uiState.screenStatus.message}")
            is ScreenStatus.Idle -> {}
        }
    }
}

@Composable
fun getMapView(
    mapViewModel: MapViewModel,
    context: Context,
    onMapReady: (MapLibreMap) -> Unit,
): MapView {
    val mapView = remember {
        // This line fixes the crash in dark mode bug, because it makes Map Libre use a Texture View
        val mode = MapLibreMapOptions.createFromAttributes(context, null)
            .textureMode(true)

        MapView(context, mode).apply { // mode are sent as argument here to fix the bug
//            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets -> //WindowInsetsCompat.CONSUMED } //bottom gap fix
//                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//                view.setPadding(
//                    view.paddingLeft,
//                    view.paddingTop,
//                    view.paddingRight,
//                    0
//                )
//                insets
//            }
            onCreate(null)

            getMapAsync { map ->
                onMapReady(map)
                setMapLayout(map, context)
                map.addOnMapClickListener { coordinate ->
                    Log.d(context.getString(R.string.t16_map), "Map clicked at: $coordinate")

                    mapViewModel.onMapClicked(
                        lat = coordinate.latitude,
                        lon = coordinate.longitude
                    )
                    true
                }
                map.addOnCameraIdleListener {
                    // Forward raw camera state to the VM; gating, throttling and
                    // bounds-expansion live there so this callback stays UDF-clean.
                    val pos = map.cameraPosition
                    val region = map.projection.visibleRegion.latLngBounds
                    mapViewModel.onCameraIdle(
                        target = pos.target,
                        zoom = pos.zoom,
                        visibleBounds = BoundingBox(
                            north = region.northEast.latitude,
                            south = region.southWest.latitude,
                            east = region.northEast.longitude,
                            west = region.southWest.longitude,
                        )
                    )
                }
            }
        }
    }
    return mapView
}

@Composable
fun LaunchEffects(
    viewModel: MapViewModel,
    uiState: MapScreenState,
    mapRef: MapLibreMap?,
    context: Context,
    locationPermissionRequest: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    locationPermissionGranted: Boolean,
    onMapLoaded: () -> Unit,
    isDarkMode: Boolean
) {
    val zoom = 12.0 // The zoom distance used for each zoom() call
    // Satellite mode
    LaunchedEffect(uiState.isSatelliteMode, mapRef, isDarkMode) {
        val currentMap = mapRef ?: return@LaunchedEffect

        val styleUrl = if (uiState.isSatelliteMode)
            "https://api.maptiler.com/maps/019dabad-5643-770e-9f49-33576ba87164/style.json?key=OXxwimrGO7F3CL0neXBq"
        else
            if (isDarkMode)
                "https://api.maptiler.com/maps/019daf6d-d281-745e-939c-9621fc2be699/style.json?key=OXxwimrGO7F3CL0neXBq"
            else
                "https://api.maptiler.com/maps/019dab82-a3ed-7f0e-8e50-339cb9610079/style.json?key=OXxwimrGO7F3CL0neXBq"

        currentMap.setStyle(styleUrl) { style ->
            Log.d("T16_MAP", "Style loaded: ${style.uri}")
            setupHeatmapLayer(style)
            setupHistoricalFindsLayer(style, context)
            setupForestLayer(style, uiState.showWmsForest)
            addPinImage(style, context)
            setupPinIcon(style)
            setupUserLocationLayer(style)
            onMapLoaded()
        }
    }
    // WMS Forest Layer
    LaunchedEffect(uiState.showWmsForest, mapRef, isDarkMode, uiState.isSatelliteMode) {
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
        mapRef,
        isDarkMode,
        uiState.isSatelliteMode
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
    LaunchedEffect(uiState.userLocation, mapRef, isDarkMode, uiState.isSatelliteMode) {
        val currentMap = mapRef ?: return@LaunchedEffect
        val location = uiState.userLocation ?: return@LaunchedEffect
        currentMap.getStyle { style ->
            val source = style.getSourceAs<GeoJsonSource>("user-location-source")
                ?: return@getStyle
            val point = Point.fromLngLat(location.longitude, location.latitude)
            source.setGeoJson(Feature.fromGeometry(point))
        }
    }
    // Mushroom finding pins
    // Updating the pins each time they change
    LaunchedEffect(
        uiState.currentContext?.historicalFinds,
        uiState.isHistoricalFindsEnabled,
        mapRef,
        isDarkMode,
        uiState.isSatelliteMode
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
            val locationSource =
                style.getSourceAs<GeoJsonSource>("historical-finds-source") ?: return@getStyle

            if (uiState.isHistoricalFindsEnabled) {
                locationSource.setGeoJson(FeatureCollection.fromFeatures(features))
            } else {
                locationSource.setGeoJson(FeatureCollection.fromFeatures(emptyList()))
            }
        }
    }

    // Reacts to a search result by animating the camera, then clears the request.
    LaunchedEffect(uiState.pendingCameraTarget, mapRef) {
        val target = uiState.pendingCameraTarget ?: return@LaunchedEffect
        val map = mapRef ?: return@LaunchedEffect
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(target.lat, target.lon), target.zoom)
        )
        viewModel.onPendingCameraTargetConsumed()
    }

    // Updating the heatMapTiles each time they change
    LaunchedEffect(
        uiState.heatmapTiles,
        uiState.isHeatmapEnabled,
        mapRef,
        isDarkMode,
        uiState.isSatelliteMode
    ) {
        val currentMap = mapRef ?: return@LaunchedEffect


        currentMap.getStyle { style ->
            if (!style.isFullyLoaded) return@getStyle

            val source = style.getSourceAs<GeoJsonSource>("heatmap-source") ?: return@getStyle

            if (uiState.isHeatmapEnabled && uiState.heatmapTiles.isNotEmpty()) {
                val features = mutableListOf<Feature>()

                val tilesToDraw = uiState.heatmapTiles.filter { tile ->
                    tile.size <= uiState.currentTileSize + 0.00001
                }

                tilesToDraw.forEach { tile ->
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
                Log.d(
                    context.getString(R.string.t16_map_heat),
                    "Drew ${features.size} grid polygons natively."
                )

            } else {
                // Clear the map safely
                source.setGeoJson(FeatureCollection.fromFeatures(emptyList()))
            }
        }
    }
    //For making the screen start on the user position
    LaunchedEffect(uiState.moveCameraToUser, mapRef) {
        if (!uiState.moveCameraToUser) return@LaunchedEffect
        val currentMap = mapRef ?: return@LaunchedEffect
        val location = uiState.userLocation ?: return@LaunchedEffect

        currentMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(location, zoom)
        )
        //resets the flag
        viewModel.onCameraMoved()
    }
    // this updates the scorecard when someone changes selected mushroom in Soppsiden
    LaunchedEffect(uiState.selectedMushroom) {
        viewModel.refreshCurrentLocation()
    }
    // Actually runs the popup and makes sure it doesn't rerun
    LaunchedEffect(Unit) {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }


    // Centers the map on the user the first time we know where they are
    // (or on Oslo if location permission is denied). Guarded by the VM-owned
    // `hasCenteredInitially` flag so it survives recomposition and rotation.
    LaunchedEffect(
        uiState.userLocation,
        locationPermissionGranted,
        mapRef,
        uiState.hasCenteredInitially
    ) {
        val map = mapRef ?: return@LaunchedEffect
        if (uiState.hasCenteredInitially) return@LaunchedEffect

        val userLat = uiState.userLocation?.latitude
        val userLon = uiState.userLocation?.longitude

        when {
            userLat != null && userLon != null -> {
                viewModel.onInitialCenterDone()
                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(userLat, userLon))
                    .zoom(zoom)
                    .build()
                onMapLoaded()
            }
            // No permission — fall back to a default location instead of
            // waiting indefinitely for a fix that will never arrive.
            !locationPermissionGranted -> {
                viewModel.onInitialCenterDone()
                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(59.91, 10.75))//Oslo
                    .zoom(zoom)
                    .build()
                onMapLoaded()
            }

            else -> {
                //Permission to location granted but no fix yet
                //Shows Oslo temporarily
                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(59.91, 10.75))
                    .zoom(zoom)
                    .build()
            }
        }
    }
}










