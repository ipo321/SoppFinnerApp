package no.uio.ifi.in2000.team16.ui.dev

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.logic.mushroom.AlgorithmType
import no.uio.ifi.in2000.team16.domain.logic.mushroom.ScorerProvider
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import javax.inject.Inject

data class DevUiState(
    val selectedMushroom: Mushroom? = null,
    val availableMushrooms: List<Mushroom> = emptyList(),
    val availableAlgorithms: List<String> = listOf("V1", "V2", "Mock", "AI"),
    val activeAlgorithm: String = "V1",
    val lastEvaluation: EvaluationResult? = null
)

@HiltViewModel
class DevViewModel @Inject constructor(
    private val speciesRepository: SpeciesRepository,
    private val scorerProvider: ScorerProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevUiState())
    val uiState: StateFlow<DevUiState> = _uiState.asStateFlow()

    init {
        val mushrooms = speciesRepository.getAllMushrooms()
        _uiState.update { it.copy(
            availableMushrooms = mushrooms,
            selectedMushroom = mushrooms.firstOrNull(),
            activeAlgorithm = scorerProvider.activeScorer.javaClass.simpleName
        ) }
    }

    fun onAlgorithmSelected(version: String) {
        val type = when(version) {
            "V1" -> AlgorithmType.V1
            "V2" -> AlgorithmType.V2
            "MOCK" -> AlgorithmType.MOCK
            else -> AlgorithmType.V2
        }

        scorerProvider.setAlgorithm(type)
        _uiState.update { it.copy(activeAlgorithm = version) }
    }
    fun onMushroomSelected(mushroom: Mushroom) {
        _uiState.update { it.copy(selectedMushroom = mushroom) }
    }

    fun recalculate(context: EnvironmentContext?) {
        if (context == null) return
        val mushroom = _uiState.value.selectedMushroom ?: return

        viewModelScope.launch {
            val result = scorerProvider.activeScorer.calculateScore(context, mushroom.profile)
            _uiState.update { it.copy(lastEvaluation = result) }
        }
    }
}