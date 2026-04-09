package no.uio.ifi.in2000.team16

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team16.data.species.SpeciesDataSource
import no.uio.ifi.in2000.team16.domain.logic.mushroom.MushroomScorer
import no.uio.ifi.in2000.team16.domain.logic.mushroom.MushroomScorerV2
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.ForestData
import no.uio.ifi.in2000.team16.domain.model.TerrainData
import no.uio.ifi.in2000.team16.domain.model.WeatherData
import no.uio.ifi.in2000.team16.domain.model.WeatherHistory
import org.junit.Test

class MushroomScorerTest {

    @Test
    fun testScore() = runTest { // runTest makes it possible to call suspend functions from test function
        val scorer: MushroomScorer = MushroomScorerV2()
        val speciesDS = SpeciesDataSource()
        val mushroom = speciesDS.allSpeciesProfiles[0] // Kantarell

        // The score should be in interval 0 <= .. <= 100
        assert(
            scorer.calculateScore(makeMockContext(), mushroom).score
                    in 0..100)
        // Score should increase when humidity is high
        assert(
            scorer.calculateScore(makeMockContext(), mushroom).score >
                    scorer.calculateScore(makeMockContext(humidity = 10.123), mushroom).score
        )
        // Score should be 0 when current month is wrong
        assertEquals("Score should be 0 when current month is wrong",
            0, scorer.calculateScore(makeMockContext(month = 1), mushroom).score)

        // Handles null data
        assert(
            scorer.calculateScore(makeMockContext(null, null, null,
                null, null, null, null, null,
                null, null, null,
                null, null, null, null,
                null, null, null, null,
                null, null, null, null,
                0, 0),
                mushroom).score
                    in 0..100
        )

        // Score should be 0 when not suitable terrain
        // Test can be added when we have terrain data
    }


    fun makeMockContext(
        temp: Double? = 8.237499999999999,
        rain: Double? = 0.0,
        humidity: Double? = 72.375,
        windSpeed: Double? = 3.0,
        dewPoint: Double? = 3.2125000000000004,
        fogFraction: Double? = 0.0,
        symbolCode: String? = "cloudy",
        probOfRain: Double? = 0.4,

        avgTempPast14Days: Double? = 2.251190473717263,
        totalRainPast14Days: Double? = 45.000000354,
        minTempPast14Days: Double? = -3.0,
        maxTempPast14Days: Double? = 9.89999962,

        terrainData: TerrainData? = null,
        ar5: String? = "Skog",

        dominantTreeSpecies: String? = "Grandominert",
        sprucePercentage: Int? = 65,
        pinePercentage: Int? = 29,
        broadleafPercentage: Int? = 6,
        fertilityCategory: String? = "Høy",
        siteIndex: Int? = 13,
        standAge: Int? = 73,
        minElevation: Int? = 330,
        maxElevation: Int? = 361,

        historicalFinds: Int = 0,
        month: Int = 8
    ): EnvironmentContext {
        return EnvironmentContext(
            currentWeather = WeatherData(
                temp = temp,
                rain = rain,
                humidity = humidity,
                windSpeed = windSpeed,
                dewPoint = dewPoint,
                fogFraction = fogFraction,
                symbolCode = symbolCode,
                probOfRain = probOfRain
            ),
            weatherHistory = WeatherHistory(
                avgTempPast14Days = avgTempPast14Days,
                totalRainPast14Days = totalRainPast14Days,
                minTempPast14Days = minTempPast14Days,
                maxTempPast14Days = maxTempPast14Days
            ),
            terrainData = terrainData,
            ar5 = ar5,
            forestData = ForestData(
                dominantTreeSpecies = dominantTreeSpecies,
                sprucePercentage = sprucePercentage,
                pinePercentage = pinePercentage,
                broadleafPercentage = broadleafPercentage,
                fertilityCategory = fertilityCategory,
                siteIndex = siteIndex,
                standAge = standAge,
                minElevation = minElevation,
                maxElevation = maxElevation
            ),
            historicalFinds = historicalFinds,
            currentMonth = month
        )
    }
}
