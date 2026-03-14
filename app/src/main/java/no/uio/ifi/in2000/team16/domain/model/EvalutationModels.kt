package no.uio.ifi.in2000.team16.domain.model

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
    val temp: Double,
    val rain: Double,
    val humidity: Double,
    val windSpeed: Double,
    //Add fog, weather history and so on
)

data class WeatherHistory(
    val tempHistory: Double,
    val rainHistory: Double,
    val humidityHistory: Double
)



data class TerrainData(
    val slope: Double,  //Slope in degrees (0- 90)
    val direction: Double //Direction in degrees (0-360)
)

data class ForestData(
    val arType: Int,    //AR5 code
    val treeSpecies: Int //type of forest
)


// Structure of the result from the algorithm
data class EvaluationResult(
    val score: Int,  // Score from 0 -100
    val description: String   // Description and reason of the score
)