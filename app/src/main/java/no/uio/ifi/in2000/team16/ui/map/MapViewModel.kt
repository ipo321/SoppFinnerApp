package no.uio.ifi.in2000.team16.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.usecase.GetEnvironmentContextUseCase
import javax.inject.Inject
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.logic.mushroom.ScorerProvider
import no.uio.ifi.in2000.team16.domain.model.BoundingBox
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.HeatmapTile
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.domain.usecase.CalculateHeatmapAreaUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.team16.data.arcGIS.ArcGISRepository
import no.uio.ifi.in2000.team16.data.arcGIS.Suggestion
import no.uio.ifi.in2000.team16.data.kartverket.Address
import no.uio.ifi.in2000.team16.data.kartverket.AddressRepository

@HiltViewModel
class MapViewModel @Inject constructor(
    private val speciesRepository: SpeciesRepository,
    //private val arcGISRepository: ArcGISRepository,
    private val addressRepository: AddressRepository,
    private val getEnvironmentContextUseCase: GetEnvironmentContextUseCase,
    private val calculateHeatmapAreaUseCase: CalculateHeatmapAreaUseCase,
    private val scorerProvider: ScorerProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapScreenState())
    val uiState : StateFlow<MapScreenState> = _uiState.asStateFlow()

    private var heatmapJob: Job? = null // cancel old calculations
    private var searchJob: Job? = null // cancel old searches
    private var savedBounds: BoundingBox? = null // makes it easy for mushroom change to update heatmap

    init{
        viewModelScope.launch {
            speciesRepository.selectedMushroom.collect { mushroom ->
                _uiState.update { it.copy(selectedMushroom = mushroom) }
                try {
                    getEnvironmentContextUseCase(
                        59.91,
                        10.75,
                        mushroom.profile
                    ) //Warmup to get scorer quicker
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
        _uiState.update { it.copy(lastClickedLat = lat, lastClickedLon = lon) }
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
                                "${occ.species ?: occ.scientificName}@${occ.decimalLatitude},${occ.decimalLongitude}"
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
        savedBounds = bounds
        val currentMushroom = _uiState.value.selectedMushroom ?: return

        heatmapJob?.cancel()

        heatmapJob = viewModelScope.launch {
            _uiState.update { it.copy(isFetchingHeatmap = true) }
            try {
                val tiles = calculateHeatmapAreaUseCase(bounds, currentMushroom.profile)

                if (tiles.isNotEmpty()) {
                    _uiState.update { it.copy(heatmapTiles = tiles, isFetchingHeatmap = false) }
                } else {
                    _uiState.update { it.copy(isFetchingHeatmap = false) }
                }
            }catch(e: CancellationException){
                throw e
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error Calc Heatmap", e)
                _uiState.update { it.copy(isFetchingHeatmap = false) }
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

    fun toggleHistoricalFinds() {
        _uiState.update { it.copy(isHistoricalFindsEnabled = !it.isHistoricalFindsEnabled) }
    }


    fun onMushroomSelected(mushroom: Mushroom) {
        viewModelScope.launch {
            speciesRepository.updateSelectedMushroom(mushroom)
            savedBounds?.let { fetchHeatmapData(it) }

            val lat = _uiState.value.lastClickedLat
            val lon = _uiState.value.lastClickedLon
            if (lat != null && lon != null) {//change mushroom trigges a onMapClicked reload
                onMapClicked(lat, lon)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun performSearch() {
        println("Søker etter: ${_uiState.value.searchQuery}")
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel() // Cancels ongoing search when search is updated
        searchJob = viewModelScope.launch { // Starts new search
            delay(300) // Makes it wait a little bit before searching, to not spam the API
            val suggestions = addressRepository.getSuggestions(query)
            _uiState.update { it.copy(addressSuggestions = suggestions) }
        }
    }

    // Method for getting location for address chosen from search, used when search completes
    fun searchAddress(address: Address, onLocationFound: (lat: Double, lon: Double) -> Unit) {
        val location = address.representasjonspunkt
        if (location?.lat != null && location.lon != null) {
            onLocationFound(location.lat, location.lon)
        }
    }

    // Method for finding address from the query, used when user is still searching
    fun searchAddressByText(query: String, onLocationFound: (Double, Double) -> Unit) {
        viewModelScope.launch {
            val location = addressRepository.getLocationCoordinates(query)
            if (location != null && location.lat != null && location.lon != null) {
                onLocationFound(location.lat, location.lon)
            }
        }
    }
}
data class MapScreenState(
    val screenStatus: ScreenStatus = ScreenStatus.Idle,
    val selectedMushroom: Mushroom? = null,
    val currentContext: EnvironmentContext? = null,
    val evaluation: EvaluationResult? = null,
    val favoriteMushrooms: List<Mushroom> = emptyList(),
    val isHeatmapEnabled: Boolean = false,
    val heatmapTiles: List<HeatmapTile> = emptyList(), // NY!
    val isFetchingHeatmap: Boolean = false,
    val searchQuery: String = "",
    val isHistoricalFindsEnabled: Boolean = false,
    val lastClickedLat: Double? = null,
    val lastClickedLon: Double? = null,
    //val addressSuggestions: List<Suggestion> = emptyList()
    val addressSuggestions: List<Address> = emptyList()
)

sealed class ScreenStatus{
    object Idle : ScreenStatus()
    object Loading : ScreenStatus()
    object Success : ScreenStatus()
    data class Error(val message: String) : ScreenStatus()
}
