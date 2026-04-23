package no.uio.ifi.in2000.team16.domain.model


/*
Dataclass for what information about the mushroom itself that goes into the algorithm
*/

data class SpeciesProfile(
    val name: String,
    val type: Int,
    val taxKey: String,
    val activeMonths: List<Int>,
    val killTemp: Double,
    val optimalTemp: Double,
    val tempToleranceC: Double,
    val optimalRainMm: Double,
    val maxCurrentRainMm: Double,
    val optimalHumidity: Double,
    val preferredTreeSpecies: List<String>,
    val minForestAge: Int,
    val preferredTerrain: List<Int>,
    val maxSlope: Double,
    val windPenaltyThreshold: Double
)
