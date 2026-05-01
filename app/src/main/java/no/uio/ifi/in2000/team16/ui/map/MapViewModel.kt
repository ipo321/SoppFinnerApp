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
    private val locationRepository: LocationRepositoryInterface,
    private val getEnvironmentContextUseCase: GetEnvironmentContextUseCase,
    private val calculateHeatmapAreaUseCase: CalculateHeatmapAreaUseCase,
    private val scorerProvider: ScorerProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapScreenState())
    val uiState : StateFlow<MapScreenState> = _uiState.asStateFlow()

    private var heatmapJob: Job? = null // cancel old calculations
    private var searchJob: Job? = null // cancel old searches
    private var savedBounds: BoundingBox? = null // makes it easy for mushroom change to update heatmap
    private var debounceJob: Job? = null
    private var hasMovedToUserLocation = false // flag to see if camera has been moved to user on startup

    init{
        viewModelScope.launch {
            speciesRepository.selectedMushroom.collect { mushroom ->
                _uiState.update { it.copy(selectedMushroom = mushroom) }
                try {
                    val env = getEnvironmentContextUseCase(
                        59.91,
                        10.75,
                        mushroom.profile
                    ) //Warmup to get scorer quicker
                    val scorer = scorerProvider.activeScorer
                    scorer.calculateScore(env, mushroom.profile)
                } catch (e: Exception) {
                }
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
                val context = getEnvironmentContextUseCase(lat, lon, currentMushroom.profile)  // Have to add species selection here as well, but needs to update usecase and
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

    fun toggleHeatmap(currentBounds: BoundingBox?) {
        val newState = !_uiState.value.isHeatmapEnabled
        _uiState.update { it.copy(isHeatmapEnabled = newState) }

        if (!newState) {
            _uiState.update { it.copy(heatmapTiles = emptyList()) }
        } else if (currentBounds != null) {
            fetchHeatmapData(currentBounds)
            savedBounds = currentBounds
        }
    }




    fun onMushroomSelected(mushroom: Mushroom) {
        viewModelScope.launch {
            speciesRepository.updateSelectedMushroom(mushroom)

            _uiState.update { it.copy(heatmapTiles = emptyList()) }
            savedBounds?.let { fetchHeatmapData(it) }

            val lat = _uiState.value.lastClickedLat
            val lon = _uiState.value.lastClickedLon
            if (lat != null && lon != null) {//change mushroom trigges a onMapClicked reload
                onMapClicked(lat, lon)
            }
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
    
    // Method for finding address from the query, used when user is still searching
    fun searchAddressByText(query: String, onLocationFound: (Double, Double) -> Unit) {
        viewModelScope.launch {
            val location = locationRepository.getLocationCoordinates(query)
            if (location != null && location.latitude != null && location.longitude != null) {
                onLocationFound(location.latitude!!, location.longitude!!)
            }
        }
    }

    // Method for getting location for address chosen from search, used when search completes
    fun searchLocation(location: SearchableLocation, onLocationFound: (lat: Double, lon: Double) -> Unit) {
        val lat = location.latitude
        val lon = location.longitude
        if (lat != null && lon != null) {
            onLocationFound(lat, lon)
        }
    }

    //Updates the users position
    fun onUserLocationUpdated(lat: Double, lon: Double){
        _uiState.update {it.copy(userLocation = LatLng(lat, lon))}

        if(!hasMovedToUserLocation){
            hasMovedToUserLocation = true
            _uiState.update {it.copy(moveCameraToUser = true)}
        }
    }

    fun onCameraMoved(){
        _uiState.update{it.copy(moveCameraToUser = false)}
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
        _uiState.update { it.copy(screenStatus = ScreenStatus.Idle, evaluation = null, currentContext = null) }
    }

    fun retryLastClick(){
        val lat = _uiState.value.lastClickedLat ?: return
        val lon = _uiState.value.lastClickedLon ?: return
        Log.d("T16_retry", "Retrying click at lat: $lat, lon: $lon")
        onMapClicked(lat, lon)
    }
}
