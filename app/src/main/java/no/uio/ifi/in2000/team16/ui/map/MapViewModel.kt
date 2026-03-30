package no.uio.ifi.in2000.team16.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomSpecies
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.usecase.GetEnvironmentContextUseCase
import javax.inject.Inject
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.logic.mushroom.ScorerProvider
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.data.species.SpeciesProfile

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
            val defaultSpecies = speciesRepository.getSpeciesByName("Skarlagen vårbeger")
            _uiState.update { it.copy(selectedSpecies = defaultSpecies) }
            try {
                getEnvironmentContextUseCase(59.91, 10.75) //Warmup to get scorer quicker
            } catch (e: Exception){
            }
        }
    }
    fun onMapClicked(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(screenStatus = ScreenStatus.Loading) }
            try {
                val profile =
                    _uiState.value.selectedSpecies ?: throw Exception("Mangler sopp-profil")
                val context = getEnvironmentContextUseCase(lat, lon, MushroomSpecies.FUNNEL_CHANTERELLE)  // Have to add species selection here as well, but needs to update usecase and
                val scorer = scorerProvider.activeScorer

                val evaluation = scorer.calculateScore(context, profile)

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

    fun selectFavoriteMushroom() {
        viewModelScope.launch {
            val fav = speciesRepository.getSpeciesByName("Kantarell") ?: return@launch
            _uiState.update { it.copy(selectedSpecies = fav) }
        }
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
        val selectedSpecies: SpeciesProfile? = null,
        val currentContext: EnvironmentContext? = null,
        val evaluation: EvaluationResult? = null,

        val isHeatmapEnabled: Boolean = false,
        val searchQuery: String = "",
        val isTilerForest: Boolean = false
    )

    sealed class ScreenStatus{
        object Idle : ScreenStatus()
        object Loading : ScreenStatus()
        object Success : ScreenStatus()
        data class Error(val message: String) : ScreenStatus()
}
