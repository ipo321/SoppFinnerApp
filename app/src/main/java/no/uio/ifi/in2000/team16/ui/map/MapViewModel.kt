package no.uio.ifi.in2000.team16.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomSpecies
import no.uio.ifi.in2000.team16.data.nibio.ForestRepository
import no.uio.ifi.in2000.team16.domain.logic.mushroom.MushroomScorerV2
import no.uio.ifi.in2000.team16.domain.model.AreaResource
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.usecase.GetEnvironmentContextUseCase
import javax.inject.Inject
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.ui.common.UiState

@HiltViewModel
class MapViewModel @Inject constructor(
    private val forestRepository: ForestRepository,
    private val SpeciesRepository: SpeciesRepository,
    private val getEnvironmentContextUseCase: GetEnvironmentContextUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<MapUiState>>(UiState.Idle)
    val uiState : StateFlow<UiState<MapUiState>> = _uiState.asStateFlow()

    // No init because we don't want data before click

    fun getForestInfo(
        pxX: Int,
        pxY: Int,
        south: Double,
        west: Double,
        north: Double,
        east: Double,
        width: Int,
        height: Int
    ){
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val forestInfo = forestRepository.getForestInfo(
                    pxX, pxY, south, west, north, east, width, height
                )
                val profile = SpeciesRepository.getSpeciesByName("Skarlagen vårbeger") ?: throw IllegalArgumentException("Species 'Skarlagen vårbeger' not found")


                //Cheap?? version to make up lon and lat just to have some nmbrs, unsure how to get them
                val lat = (south + north) / 2
                val lon = (west + east) / 2

                val context = getEnvironmentContextUseCase(
                    lat,
                    lon,
                    MushroomSpecies.FUNNEL_CHANTERELLE
                )

                val scorer = MushroomScorerV2()

                val evalutation = scorer.calculateScore(context, profile)

                _uiState.value = UiState.Success(
                    MapUiState(forestInfo, evalutation)
                )

            } catch (e: Exception) {
                _uiState.value = UiState.Error
            }
        }
    }


}

data class MapUiState(
    val areaResource: AreaResource,
    val evaluation: EvaluationResult)