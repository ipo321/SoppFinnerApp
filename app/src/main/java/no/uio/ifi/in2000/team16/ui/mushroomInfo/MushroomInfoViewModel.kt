package no.uio.ifi.in2000.team16.ui.mushroomInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.species.SpeciesRepositoryInterface
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import javax.inject.Inject

/*
viwemodel for both the mushroom listScreen and the MushroomInfoscreen.
gets from speciesRepository that gets form local data that dont change.

 */
data class MushroomInfoUiState(
    val favorites: List<Mushroom> = emptyList(),
    val otherMushrooms: List<Mushroom> = emptyList(),
    val viewMushroom: Mushroom? = null,
    val selectedMushroom: Mushroom? = null,
    val isInSeason: Boolean = false
)

@HiltViewModel
class MushroomInfoViewModel @Inject constructor(
    private val repository: SpeciesRepositoryInterface
) : ViewModel() {


    private val _uiState = MutableStateFlow(
        MushroomInfoUiState()
    )
    val uiState: StateFlow<MushroomInfoUiState> = _uiState.asStateFlow()
    init {
        // Load static mushroom list once
        viewModelScope.launch {
            val allMushrooms = repository.getAllMushrooms()
            _uiState.update {
                it.copy(
                    // mushrooms = allMushrooms,
                    otherMushrooms = allMushrooms,
                    selectedMushroom = allMushrooms.firstOrNull()
                )
            }
            repository.favoriteMushrooms.collect { favs ->
                val favNames = favs.map { it.commonName }.toSet()
                val favoriteList = allMushrooms.filter { it.commonName in favNames }
                val otherList = allMushrooms.filterNot { it.commonName in favNames }
                _uiState.update {
                    it.copy(
                        favorites = favoriteList,
                        otherMushrooms = otherList
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.selectedMushroom.collect { mushroom ->
                _uiState.update { it.copy(selectedMushroom = mushroom) }
            }
        }
    }

    fun onMushroomSelected(mushroom: Mushroom) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedMushroom = mushroom) }
            repository.updateSelectedMushroom(mushroom)
        }
    }

        // Collect favorites into uiState

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


