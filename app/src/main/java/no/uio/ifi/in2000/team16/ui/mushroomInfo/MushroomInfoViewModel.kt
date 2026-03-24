package no.uio.ifi.in2000.team16.ui.mushroomInfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import no.uio.ifi.in2000.team16.R

class MushroomInfoViewModel : ViewModel(){
    val mushrooms = listOf(
        Mushroom("Kantarell", "Cantharellus cibarius", R.drawable.chanterelle),
        Mushroom("Steinsopp", "Boletus edulis", R.drawable.rockfungus))

    var selectedMushroom by mutableStateOf(mushrooms[0])
        private set

    fun selectMushroom(mushroom: Mushroom) {
        selectedMushroom = mushroom
    }
}

data class Mushroom(
    val commonName: String,
    val latinName: String,
    val imageRes: Int
)