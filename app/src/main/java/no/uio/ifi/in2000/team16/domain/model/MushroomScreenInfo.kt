package no.uio.ifi.in2000.team16.domain.model

import no.uio.ifi.in2000.team16.R


data class MushroomProperty(   // was MushroomIcon
    val label: String,
    val iconRes: Int,
    val isNegative: Boolean = false  // was isRed
)

data class MushroomDisplay(    // was Mushroom (to avoid confusion with MushroomInfo)
    val commonName: String,
    val latinName: String,
    val imageRes: Int,
    val properties: List<MushroomProperty>
)

val allMushrooms = listOf(
    MushroomDisplay(
        commonName = "Kantarell",
        latinName = "Cantharellus cibarius",
        imageRes = R.drawable.chanterelle,
        properties = listOf(
            MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
            MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
        )
    ),
    MushroomDisplay(
        commonName = "Steinsopp",
        latinName = "Boletus edulis",
        imageRes = R.drawable.rockfungus,
        properties = listOf(
            MushroomProperty("Giftig", R.drawable.skull, isNegative = true),
            MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
        )
    )
)

