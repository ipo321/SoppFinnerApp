package no.uio.ifi.in2000.team16.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.Mushroom

@HiltViewModel
class MapViewModel @Inject constructor(
    private val speciesRepository: SpeciesRepository,
    private val getEnvironmentContextUseCase: GetEnvironmentContextUseCase,
    private val scorerProvider: ScorerProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapScreenState())
    val uiState : StateFlow<MapScreenState> = _uiState.asStateFlow()

    init{
        viewModelScope.launch {
            val defaultMushroom = speciesRepository.getMushroomByName("Skarlagen vårbeger")
            _uiState.update { it.copy(selectedMushroom = defaultMushroom) }
            try {
                getEnvironmentContextUseCase(59.91, 10.75, defaultMushroom.profile) //Warmup to get scorer quicker
            } catch (e: Exception){
            }
        }
    }
    fun onMapClicked(lat: Double, lon: Double) {
        val currentMushroom = _uiState.value.selectedMushroom ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(screenStatus = ScreenStatus.Loading) }
            try {
                val context = getEnvironmentContextUseCase(lat, lon, currentMushroom.profile)  // Have to add species selection here as well, but needs to update usecase and
                val scorer = scorerProvider.activeScorer
                val evaluation = scorer.calculateScore(context, currentMushroom.profile)

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
    fun toggleHeatmap() {
        _uiState.update { it.copy(isHeatmapEnabled = !it.isHeatmapEnabled) }
    }

    fun onMushroomSelected(mushroom: Mushroom) {
        _uiState.update { it.copy(selectedMushroom = mushroom) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun performSearch() {
        println("Søker etter: ${_uiState.value.searchQuery}")
    }

}


    data class MapScreenState(
        val screenStatus: ScreenStatus = ScreenStatus.Idle,
        val selectedMushroom: Mushroom? = null,
        val currentContext: EnvironmentContext? = null,
        val evaluation: EvaluationResult? = null,

        val isHeatmapEnabled: Boolean = false,
        val searchQuery: String = ""
    )

    sealed class ScreenStatus{
        object Idle : ScreenStatus()
        object Loading : ScreenStatus()
        object Success : ScreenStatus()
        data class Error(val message: String) : ScreenStatus()
}
