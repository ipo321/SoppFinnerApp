package no.uio.ifi.in2000.team16.domain.model


/*
Dataclasses for handling the information that will be put into the evaluation module. Important to look at for all API and repositories as this is what your repository should return.
*/

data class EnvironmentContext(
    val currentWeather: WeatherData?,
    val weatherHistory: WeatherHistory?,
    val terrainData: TerrainData?,
    val forestData: ForestData?,
    val historicalFinds: Int = 0,
    val currentMonth: Int
) {
    companion object {
        val Mock = EnvironmentContext(
            currentWeather = null,
            weatherHistory = null,
            terrainData = null,
            forestData = null,
            historicalFinds = 0,
            currentMonth = 8 //August
        )

    }
}

data class WeatherData(
    val temp: Double?,
    val rain: Double?,
    val humidity: Double?,
    val windSpeed: Double?,
    val dewPoint: Double?,
    val fogFraction: Double?,

    val symbolCode: String?,
    val probOfRain: Double?
    //Add fog, weather history and so on
)

data class WeatherHistory(
    val avgTempPast14Days: Double?,
    val totalRainPast14Days: Double?,
)



data class TerrainData(
    val slope: Double,  //Slope in degrees (0- 90) has to be calculated from the points
    val direction: Double //Direction in degrees (0-360) has to be calculated from the points
)

data class ForestData(
    // Trees
    val dominantTreeSpecies: String?,
    val sprucePercentage: Int?,       // 0 - 100
    val pinePercentage: Int?,         // 0 - 100
    val broadleafPercentage: Int?,    // 0 - 100

    // Soil & Growth
    val fertilityCategory: String?,
    val siteIndex: Int?,              // e.g., 8, 11, 14, 17, 20

    // Age
    val standAge: Int?,               // Age of the trees in years

    // Terrain (Elevation)
    val minElevation: Int?,           // Meters above sea level
    val maxElevation: Int?
)



// Structure of the result from the algorithm
data class EvaluationResult(
    val score: Int,  // Score from 0 -100
    val description: String   // Description and reason of the score
)