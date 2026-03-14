package no.uio.ifi.in2000.team16.domain.model

data class SpeciesProfile(
    val name: String,
    val type: Int,
    val activeMonths: List<Int>,
    val killTemp: Double,
    val optimalTemp: Double,
    val tempToleranceC: Double,
    val optimalRainMm: Double,
    val preferredTerrain: List<Int>,
    val maxSlope: Double,
    val windPenaltyThreshold: Double
) {
    companion object{
        val Mock = SpeciesProfile(
            name = "TestName",
            type = 1,
            activeMonths = listOf(7,8,9),
            killTemp = 0.0,
            optimalTemp = 15.0,
            tempToleranceC = 10.0,
            optimalRainMm = 0.0,
            preferredTerrain = listOf(1,2,3),
            maxSlope = 45.0,
            windPenaltyThreshold = 10.0
        )

    }
}
