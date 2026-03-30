package no.uio.ifi.in2000.team16.data.species

data class SpeciesProfile(
    val name: String,
    val type: Int,
    val activeMonths: List<Int>,

    //Temperature
    val killTemp: Double,
    val optimalTemp: Double,
    val tempToleranceC: Double,

    // Humidity and History
    val optimalRainMm: Double,
    val maxCurrentRainMm: Double,
    val optimalHumidity: Double,

    //Forest
    val preferredTreeSpecies: List<String>,
    val minForestAge: Int,

    //Terrain
    val preferredTerrain: List<Int>,
    val maxSlope: Double,
    val windPenaltyThreshold: Double
) {
    companion object{
        val mock = SpeciesProfile(
            name = "TestName",
            type = 1,
            activeMonths = listOf(7,8,9),

            killTemp = 0.0,
            optimalTemp = 15.0,
            tempToleranceC = 10.0,

            optimalRainMm = 0.0,
            maxCurrentRainMm = 0.0,
            optimalHumidity = 80.0,

            preferredTreeSpecies = listOf("Grandominert","Furudominert"),
            minForestAge = 10,

            preferredTerrain = listOf(1,2,3),
            maxSlope = 45.0,
            windPenaltyThreshold = 10.0
        )

    }
}