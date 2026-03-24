package no.uio.ifi.in2000.team16.ui.mushroomInfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import no.uio.ifi.in2000.team16.R

class MushroomInfoViewModel : ViewModel(){
    val mushrooms = listOf(
        Mushroom("Kantarell",
            "Cantharellus cibarius",
            R.drawable.chanterelle,
            listOf(
                MushroomIcon("Ikke giftig", R.drawable.skull, isRed = false),
                MushroomIcon("God matsopp", R.drawable.pan, isRed = false)

                )
            ),
        Mushroom("Steinsopp",
            "Boletus edulis",
            R.drawable.rockfungus,
            listOf(
                MushroomIcon("Giftig", R.drawable.skull, isRed = true),
                MushroomIcon("God matsopp", R.drawable.pan, isRed = false))
        )
    )
    var selectedMushroom by mutableStateOf(mushrooms[0])
        private set

    fun selectMushroom(mushroom: Mushroom) {
        selectedMushroom = mushroom
    }
}

data class MushroomIcon(
    val label: String,
    val iconRes: Int,
    val isRed: Boolean = false  // true = red, false = green
)

data class Mushroom(
    val commonName: String,
    val latinName: String,
    val imageRes: Int,
    val properties: List<MushroomIcon>
)