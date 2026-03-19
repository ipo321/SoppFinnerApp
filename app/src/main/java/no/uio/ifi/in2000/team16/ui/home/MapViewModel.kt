package no.uio.ifi.in2000.team16.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.nibio.ForestRepository
import no.uio.ifi.in2000.team16.domain.model.AreaResource
import org.maplibre.android.maps.MapLibreMap
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val forestRepository: ForestRepository
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
                _uiState.value = UiState.Success(MapUiState(forestInfo))
            } catch (e: Exception) {
                _uiState.value = UiState.Error
            }
        }
    }
}

data class MapUiState(val areaResource: AreaResource)