package no.uio.ifi.in2000.team16.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomRepository
import no.uio.ifi.in2000.team16.data.kartverket.LocationRepositoryInterface
import no.uio.ifi.in2000.team16.data.kartverket.SearchableLocation
import no.uio.ifi.in2000.team16.data.species.SpeciesRepositoryInterface
import no.uio.ifi.in2000.team16.domain.logic.mushroom.ScorerProvider
import no.uio.ifi.in2000.team16.domain.model.BoundingBox
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.domain.usecase.CalculateHeatmapAreaUseCase
import no.uio.ifi.in2000.team16.domain.usecase.GetEnvironmentContextUseCase
import org.maplibre.android.geometry.LatLng
import javax.inject.Inject


/*
Viewmodel for the Map screen

 */


@HiltViewModel
class MapViewModel @Inject constructor(
    private val speciesRepository: SpeciesRepositoryInterface,
    private val mushroomRepository: MushroomRepository,
    private val locationRepository: LocationRepositoryInterface,
    private val getEnvironmentContextUseCase: GetEnvironmentContextUseCase,
    private val calculateHeatmapAreaUseCase: CalculateHeatmapAreaUseCase,
    private val scorerProvider: ScorerProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapScreenState())
    val uiState : StateFlow<MapScreenState> = _uiState.asStateFlow()

    private var heatmapJob: Job? = null // cancel old calculations

    // Last fetch position — used to skip near-duplicate fetches when the camera barely moved.
    private var lastIdleTarget: LatLng? = null
    private var lastIdleZoom = 0.0
    private var lastVisibleBounds: BoundingBox? = null// Latest viewport, kept fresh so toggleHeatmap can fetch on its own
    private var searchJob: Job? = null // cancel old searches
    private var savedBounds: BoundingBox? = null // makes it easy for mushroom change to update heatmap
    private var debounceJob: Job? = null
    private var hasMovedToUserLocation = false // flag to see if camera has been moved to user on startup

    init{
        viewModelScope.launch {
            speciesRepository.selectedMushroom.collect { mushroom ->
                _uiState.update { it.copy(selectedMushroom = mushroom, heatmapTiles = emptyList()) }
                if (_uiState.value.isHeatmapEnabled) {
                    savedBounds?.let { fetchHeatmapData(it) }
                }
                try {
                    val env = getEnvironmentContextUseCase(
                        59.91,
                        10.75,
                        mushroom.profile
                    ) //Warmup to get scorer quicker
                    val scorer = scorerProvider.activeScorer
                    scorer.calculateScore(env, mushroom.profile)
                } catch (_: Exception) {
                    // Warmup is best-effort; failure here is fine, the real call will surface errors.
                }
                fetchMushroomMarkers()
            }
        }

        viewModelScope.launch {
            speciesRepository.favoriteMushrooms.collect { favs ->
                _uiState.update { it.copy(favoriteMushrooms = favs.take(3)) }
            }
        }
    }

    fun onMapClicked(lat: Double, lon: Double) {
        _uiState.update { it.copy(lastClickedLat = lat, lastClickedLon = lon, isSearchBarExpanded = false) }
        val currentMushroom = _uiState.value.selectedMushroom ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(screenStatus = ScreenStatus.Loading) }
            try {
                val context = getEnvironmentContextUseCase(lat, lon, currentMushroom.profile)
                val scorer = scorerProvider.activeScorer
                val evaluation = scorer.calculateScore(context, currentMushroom.profile)


                Log.d(
                    "T16_MapViewModel_MushroomLocation",
                    "Clicked ($lat, $lon) -> ${context.historicalFindCount} occurrences: " +
                            context.historicalFinds.take(2).joinToString { occ ->
                                "${occ.localSpeciesId ?: occ.localSpeciesId}@${occ.decimalLatitude},${occ.decimalLongitude}"
                            }
                )
                _uiState.update {
                    it.copy(
                        screenStatus = ScreenStatus.Success,
                        currentContext = context,
                        evaluation = evaluation
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        screenStatus = ScreenStatus.Error(
                            e.message ?: "Ukjent feil")) }
            }
        }
    }
    fun fetchHeatmapData(bounds: BoundingBox) {
        savedBounds = bounds
        if (!_uiState.value.isHeatmapEnabled) return
        val currentMushroom = _uiState.value.selectedMushroom ?: return

        debounceJob?.cancel()

        debounceJob = viewModelScope.launch {
            delay(500)
            heatmapJob?.cancel()

            heatmapJob = viewModelScope.launch {
                _uiState.update { it.copy(isFetchingHeatmap = true) }

                try {
                    val newTiles = calculateHeatmapAreaUseCase(
                        bounds = bounds,
                        profile = currentMushroom.profile,
                        existingTiles = _uiState.value.heatmapTiles
                    )

                    if (newTiles.isNotEmpty()) {
                        val currentSize = newTiles.first().size

                        _uiState.update { currentState ->
                            val filteredOldTiles = currentState.heatmapTiles.filter {
                                kotlin.math.abs(it.size - currentSize) < 0.0001
                            }

                            val combinedTiles = (newTiles + filteredOldTiles)
                                .distinctBy {
                                    String.format(java.util.Locale.US, "%.5f_%.5f", it.lat, it.lon)
                                }
                                .take(4000)  //Delete for entries longer

                            currentState.copy(
                                heatmapTiles = combinedTiles,
                                currentTileSize = currentSize
                            )
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("T16_MapViewModel_Heat", "Error Calc Heatmap", e)
                } finally {
                    _uiState.update { it.copy(isFetchingHeatmap = false) }
                }
            }
        }
    }

  fun toggleHeatmap() {
      val newState = !_uiState.value.isHeatmapEnabled
      _uiState.update { it.copy(isHeatmapEnabled = newState) }

      if (!newState) {
          _uiState.update { it.copy(heatmapTiles = emptyList()) }
      } else {
          lastVisibleBounds?.let { fetchHeatmapData(it) }
      }
  }



    fun onMushroomSelected(mushroom: Mushroom) {
        viewModelScope.launch {
            speciesRepository.updateSelectedMushroom(mushroom)

            _uiState.update { it.copy(heatmapTiles = emptyList()) }
            savedBounds?.let { fetchHeatmapData(it) }

            refreshCurrentLocation()
            fetchMushroomMarkers()
        }
    }
    
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel() // Cancels ongoing search when search is updated
        if (query.length < 3) {
            _uiState.update { it.copy(searchSuggestions = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch { // Starts new search
            delay(200) // Makes it wait a little bit before searching, to not spam the API
            val results = locationRepository.search(query)
            _uiState.update { it.copy(searchSuggestions = results) }
        }
    }

    fun searchAddressByText(query: String) {
        viewModelScope.launch {
            val location = locationRepository.getLocationCoordinates(query)
            val lat = location?.latitude
            val lon = location?.longitude
            if (lat != null && lon != null) {
                _uiState.update {
                    it.copy(pendingCameraTarget = PendingCameraTarget(lat, lon, zoom = 15.0))
                }
            }
        }
    }

    fun searchLocation(location: SearchableLocation) {
        val lat = location.latitude
        val lon = location.longitude
        if (lat != null && lon != null) {
            _uiState.update {
                it.copy(pendingCameraTarget = PendingCameraTarget(lat, lon, zoom = 13.0))
            }
        }
    }

    fun onPendingCameraTargetConsumed() {
        _uiState.update { it.copy(pendingCameraTarget = null) }
    }

    //Updates the users position
    fun onUserLocationUpdated(lat: Double, lon: Double){
        _uiState.update {it.copy(userLocation = LatLng(lat, lon))}

        if(!hasMovedToUserLocation){
            hasMovedToUserLocation = true
            _uiState.update {it.copy(moveCameraToUser = true)}
        }
    }
    fun centerOnUser() {
        val location = _uiState.value.userLocation ?: return
        _uiState.update {
            it.copy(
                pendingCameraTarget = PendingCameraTarget(
                    lat = location.latitude,
                    lon = location.longitude,
                    zoom = 13.0
                ),
                isSearchBarExpanded = false
            )
        }
    }
    fun onCameraMoved(){
        _uiState.update{it.copy(moveCameraToUser = false)}
    }

    /**
    * Expands the visible bounds before requesting heatmap data
    * so the fetched area is slightly larger than what's on screen.
     */
    fun onCameraIdle(target: LatLng?, zoom: Double, visibleBounds: BoundingBox) {
        lastVisibleBounds = visibleBounds
        // Heatmap is only computed at zoom 10 or closer when panning.
        if (!_uiState.value.isHeatmapEnabled || zoom < 10) return

        val last = lastIdleTarget
        if (last != null && target != null) {
            val distance = target.distanceTo(last)
            val zoomDiff = kotlin.math.abs(zoom - lastIdleZoom)
            if (distance < 200.0 && zoomDiff < 0.5) return
        }
        lastIdleTarget = target
        lastIdleZoom = zoom

        val latDiff = visibleBounds.north - visibleBounds.south
        val lonDiff = visibleBounds.east - visibleBounds.west
        val expanded = BoundingBox(
            north = visibleBounds.north + latDiff * 0.2,
            south = visibleBounds.south - latDiff * 0.2,
            east  = visibleBounds.east  + lonDiff * 0.4,
            west  = visibleBounds.west  - lonDiff * 0.4,
        )
        fetchHeatmapData(expanded)
    }

    fun onInitialCenterDone() {
        _uiState.update { it.copy(hasCenteredInitially = true) }
    }

    fun toggleSatellite() {
        _uiState.update { it.copy(isSatelliteMode = !it.isSatelliteMode) }
    }

    fun toggleWmsForest() {
        _uiState.update { it.copy(showWmsForest = !it.showWmsForest) }
    }

    fun toggleHistoricalFinds() {
        _uiState.update { it.copy(isHistoricalFindsEnabled = !it.isHistoricalFindsEnabled) }
    }

    fun setSearchBarExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isSearchBarExpanded = expanded) }
    }

    fun dismissEvaluation() {
        _uiState.update { it.copy(screenStatus = ScreenStatus.Idle, evaluation = null) }
    }

    fun refreshCurrentLocation(){
        val lat = _uiState.value.lastClickedLat ?: return
        val lon = _uiState.value.lastClickedLon ?: return
        val currentMushroom = _uiState.value.selectedMushroom ?: return

        viewModelScope.launch{
            _uiState.update{it.copy(screenStatus = ScreenStatus.Loading)}
            try{
                val context = getEnvironmentContextUseCase(lat, lon, currentMushroom.profile)
                val scorer = scorerProvider.activeScorer
                val evaluation = scorer.calculateScore(context, currentMushroom.profile)
                _uiState.update {
                    it.copy(
                        screenStatus = ScreenStatus.Success,
                        currentContext = context,
                        evaluation = evaluation
                    )
                }
            } catch(e: Exception){
                _uiState.update{it.copy(screenStatus = ScreenStatus.Error(e.message ?: "Ukjent feil"))}
            }
        }
    }

    fun retryLastClick(){
        Log.d("T16_retry", "Retrying click at lat: ${_uiState.value.lastClickedLat}, lon: ${_uiState.value.lastClickedLon}")
        refreshCurrentLocation()
    }


    fun fetchMushroomMarkers() {
        val mushroom = uiState.value.selectedMushroom ?: return
        viewModelScope.launch {
            val result = mushroomRepository.getMushroomsNearLocation(
                lat = 60.0, lon = 10.0,
                radiusKm = 20000.0,
                mushroomType = mushroom.profile.name
            )
            _uiState.update { it.copy(historicalFindMarkers = result.results) }
        }
    }
}
