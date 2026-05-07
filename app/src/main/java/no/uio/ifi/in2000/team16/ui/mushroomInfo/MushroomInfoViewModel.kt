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
 * ViewModel shared by the mushroom list screen and the mushroom info screen.
 *
 * The full mushroom catalog is static and loaded once from SpeciesRepository
 * at init. Favorites are observed as a Flow so the UI updates when the user
 * stars/unstars a mushroom from anywhere in the app.
 */

/**
 * UI state for the mushroom list and info screens.
 *
 * @property favorites          mushrooms the user has marked as favorites
 * @property otherMushrooms     all remaining (non-favorite) mushrooms
 * @property viewMushroom       the mushroom currently being shown on the info screen
 * @property selectedMushroom   the mushroom currently active in the rest of the app
 *                              (e.g. the one whose finds are drawn on the map)
 * @property isInSeason         whether [viewMushroom] is in season this month
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

    private val _uiState = MutableStateFlow(MushroomInfoUiState())
    val uiState: StateFlow<MushroomInfoUiState> = _uiState.asStateFlow()

    init {
        // Load the static catalog once, then keep favorites in sync with the
        // repository. Mushrooms are split into two lists (favorites / others)
        // so the UI can render them as separate sections without re-filtering
        // on every recomposition.
        viewModelScope.launch {
            val allMushrooms = repository.getAllMushrooms()
            _uiState.update { it.copy(otherMushrooms = allMushrooms) }
            // Re-partitions the catalog every time the favorites flow emits.
            // Matching is done by commonName since favorites are stored as
            // a separate (smaller) entity in the repository.
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

        // The selected mushroom is shared app-wide via the repository so other
        // screens (e.g. the map) can react when it changes here.
        viewModelScope.launch {
            repository.selectedMushroom.collect { mushroom ->
                _uiState.update { it.copy(selectedMushroom = mushroom) }
            }
        }
    }

    /**
     * Called when the user picks a mushroom from the list.
     *
     * Pushes the choice to the repository — the repository.selectedMushroom
     * flow collected in init will propagate it back into [uiState], and any
     * other screen observing the same flow (e.g. the map) will react too.
     */
    fun onMushroomSelected(mushroom: Mushroom) {
        viewModelScope.launch {
            repository.updateSelectedMushroom(mushroom)
        }
    }

    /**
     * Called when the user opens the info screen for a mushroom.
     *
     * Also computes whether the mushroom is in season this month so the UI
     * can show an "in season" badge without doing date math itself.
     */
    fun onMushroomViewing(mushroom: Mushroom) {
        val currentMonth = java.util.Calendar.getInstance()
            .get(java.util.Calendar.MONTH) + 1   // Calendar.MONTH is 0-based; activeMonths uses 1–12
        _uiState.update {
            it.copy(
                viewMushroom = mushroom,
                isInSeason = mushroom.profile.activeMonths.contains(currentMonth)
            )
        }
    }

    /**
     * favorites or unfavorites a mushroom. The favorites flow collected in init
     * will re-emit and the favorites/others lists in the UI state will
     * update automatically.
     */
    fun toggleFavorite(mushroom: Mushroom) {
        viewModelScope.launch {
            repository.toggleFavorite(mushroom)
        }
    }
}