package no.uio.ifi.in2000.team16.domain.model

data class Mushroom(
    val commonName: String,
    val latinName: String,
    val imageRes: Int,
    val bioRes: String,
    val properties: List<MushroomProperty>,
    val profile: SpeciesProfile
)

data class MushroomProperty(
    val label: String,
    val iconRes: Int,
    val isNegative: Boolean = false
)