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

@HiltViewModel
class MapViewModel @Inject constructor(
    private val speciesRepository: SpeciesRepository,
    private val getEnvironmentContextUseCase: GetEnvironmentContextUseCase,
    private val calculateHeatmapAreaUseCase: CalculateHeatmapAreaUseCase,
    private val scorerProvider: ScorerProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapScreenState())
    val uiState : StateFlow<MapScreenState> = _uiState.asStateFlow()

    private var heatmapJob: Job? = null // cancel old calculations
    private var savedBounds: BoundingBox? = null // makes it easy for mushroom change to update heatmap
    private var lastClickedLat: Double? = null  // makes it easy for mushroom change to update location pins and scorecard
    private var lastClickedLon: Double? = null
    private var debounceJob: Job? = null

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
        lastClickedLat = lat
        lastClickedLon = lon
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

    fun toggleHistoricalFinds() {
        _uiState.update { it.copy(isHistoricalFindsEnabled = !it.isHistoricalFindsEnabled) }
    }


    fun onMushroomSelected(mushroom: Mushroom) {
        viewModelScope.launch {
            speciesRepository.updateSelectedMushroom(mushroom)

            _uiState.update { it.copy(heatmapTiles = emptyList()) }
            savedBounds?.let { fetchHeatmapData(it) }

            if (lastClickedLat != null && lastClickedLon != null) {//change mushroom trigges a onMapClicked reload
                onMapClicked(lastClickedLat!!, lastClickedLon!!)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun performSearch() {
        println("Søker etter: ${_uiState.value.searchQuery}")
    }

    fun toggleSatellite() {
        _uiState.update { it.copy(isSatelliteMode = !it.isSatelliteMode) }
    }

    fun toggleWmsForest() {
        _uiState.update { it.copy(showWmsForest = !it.showWmsForest) }
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
        val currentTileSize: Double = 0.2,
        val isSatelliteMode: Boolean = false,
        val showWmsForest: Boolean = false
    )

sealed class ScreenStatus{
    object Idle : ScreenStatus()
    object Loading : ScreenStatus()
    object Success : ScreenStatus()
    data class Error(val message: String) : ScreenStatus()
}
