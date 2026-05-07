package no.uio.ifi.in2000.team16.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.logic.mushroom.AlgorithmType
import no.uio.ifi.in2000.team16.domain.logic.mushroom.ScorerProvider
import no.uio.ifi.in2000.team16.domain.logic.mushroom.toUiName
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import javax.inject.Inject

data class AlgorithmUiState(
    val selectedMushroom: Mushroom? = null,
    val availableMushrooms: List<Mushroom> = emptyList(),
    val availableAlgorithms: List<String> = listOf("ML", "Forenklet", "KI"),
    val activeAlgorithm: String = "ML",
    val lastEvaluation: EvaluationResult? = null
)

@HiltViewModel
class AlgorithmViewModel @Inject constructor(
    private val speciesRepository: SpeciesRepository,
    private val scorerProvider: ScorerProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlgorithmUiState())
    val uiState: StateFlow<AlgorithmUiState> = _uiState.asStateFlow()


    init {
        val allMushrooms = speciesRepository.getAllMushrooms()
        val currentType = scorerProvider.getActiveAlgorithm()
        _uiState.update { it.copy(
            availableMushrooms = allMushrooms,
            activeAlgorithm = currentType.toUiName()
        )}
        viewModelScope.launch {
            speciesRepository.selectedMushroom.collect { globalSelectedMushroom ->
                _uiState.update { it.copy(selectedMushroom = globalSelectedMushroom) }
            }
        }
    }

    fun onAlgorithmSelected(version: String) {
        val type = version.toAlgorithmType()
        scorerProvider.setAlgorithm(type)
        _uiState.update { it.copy(activeAlgorithm = version) }
    }

    // Maps the string type to the enum type
    private fun String.toAlgorithmType(): AlgorithmType =
        when (this) {
            "Lokal" -> AlgorithmType.BASIC
            "ML"    -> AlgorithmType.ML
            "KI"    -> AlgorithmType.AI
            else    -> AlgorithmType.ML // ML is default
        }
}