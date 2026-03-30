package no.uio.ifi.in2000.team16.ui.mushroomInfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import javax.inject.Inject


@HiltViewModel
class MushroomInfoViewModel @Inject constructor(
    private val repository: SpeciesRepository
) : ViewModel() {

    val mushrooms = repository.getAllMushrooms()
    var selectedMushroom by mutableStateOf(mushrooms[0])
        private set

    fun selectMushroom(mushroom: Mushroom) {
        selectedMushroom = mushroom
    }
}


