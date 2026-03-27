package no.uio.ifi.in2000.team16.ui.mushroomInfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.data.species.SpeciesDataSource

class MushroomInfoViewModel : ViewModel(){

    val source: SpeciesDataSource = SpeciesDataSource()
   val mushrooms = source.getAllMushroomInfos()
    var selectedMushroom by mutableStateOf(mushrooms[0])
        private set

    fun selectMushroom(mushroom: SpeciesDataSource.MushroomInfo) {
        selectedMushroom = mushroom
    }
}
