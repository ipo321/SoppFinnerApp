package no.uio.ifi.in2000.team16.ui.mushroomInfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import javax.inject.Inject
import java.time.LocalDate

/*
viwemodel for both the mushroom listScreen and the MushroomInfoscreen.
gets from speciesRepository that gets form local data that dont change.

 */
data class MushroomInfoUiState(
    val mushrooms: List<Mushroom> = emptyList(),
    val favorites: List<Mushroom> = emptyList(),
    val viewMushroom: Mushroom? = null,
    val selectedMushroom: Mushroom? = null,
    val isInSeason: Boolean = false
)

@HiltViewModel
class MushroomInfoViewModel @Inject constructor(
    private val repository: SpeciesRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(
        MushroomInfoUiState()
    )
    val uiState: StateFlow<MushroomInfoUiState> = _uiState.asStateFlow()
    fun onMushroomSelected(mushroom: Mushroom) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedMushroom = mushroom) }
            repository.updateSelectedMushroom(mushroom)
        }
    }
    init {
        // Load static mushroom list once
        val allMushrooms = repository.getAllMushrooms()
        _uiState.update {
            it.copy(
                mushrooms = allMushrooms,
                selectedMushroom = allMushrooms.firstOrNull()
            )
        }

        // Collect favorites into uiState
        viewModelScope.launch {
            repository.favoriteMushrooms.collect { favs ->
                _uiState.update { it.copy(favorites = favs) }
            }
        }
    }

    fun onMushroomViewing(mushroom: Mushroom) {
        val currentMonth = java.util.Calendar.getInstance()
            .get(java.util.Calendar.MONTH) + 1
        _uiState.update {
            it.copy(
                viewMushroom = mushroom,
                isInSeason = mushroom.profile.activeMonths.contains(currentMonth)
            )
        }
    }

    fun toggleFavorite(mushroom: Mushroom) {
        viewModelScope.launch {
            repository.toggleFavorite(mushroom)
        }
    }
}

    //cheking if the Mushroom is in season used for the badge
    /*val isInSeason: Boolean
        get() {
            val currentMonth = java.util.Calendar.getInstance()
                .get(java.util.Calendar.MONTH) + 1//calendar is 0 indeksed

            return viewMushroom?.profile?.activeMonths?.contains(currentMonth) == true
        }

}*/


