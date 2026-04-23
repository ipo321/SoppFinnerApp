package no.uio.ifi.in2000.team16.ui.mushroomInfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import javax.inject.Inject
import java.time.LocalDate



@HiltViewModel
class MushroomInfoViewModel @Inject constructor(
    private val repository: SpeciesRepository
) : ViewModel() {

    val mushrooms = repository.getAllMushrooms()
    var viewMushroom by mutableStateOf<Mushroom?>(null)
        private set
    var selectedMushroom by mutableStateOf<Mushroom?>(
        if (mushrooms.isNotEmpty()) mushrooms[0] else null
    )
        private set
    val favoriteMushrooms: Flow<List<Mushroom>> = repository.favoriteMushrooms
    fun onMushroomSelected(mushroom: Mushroom) {
        viewModelScope.launch {
            selectedMushroom = mushroom
            repository.updateSelectedMushroom(mushroom)
        }
    }
    fun onMushroomViewing(mushroom: Mushroom) {
        viewMushroom = mushroom
    }
    fun toggleFavorite(mushroom: Mushroom) {
        viewModelScope.launch {
            repository.toggleFavorite(mushroom)
        }
    }

    val isInSeason: Boolean
        get() {
            val currentMonth = java.util.Calendar.getInstance()
                .get(java.util.Calendar.MONTH) + 1

            return viewMushroom?.profile?.activeMonths?.contains(currentMonth) == true
        }

}


