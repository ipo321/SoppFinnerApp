package no.uio.ifi.in2000.team16.domain.logic

import kotlin.math.abs

//Scorers are used to build up the algorithm. They can be combined in different ways to create different types. They will all return a value between 0.0 and 1.0 wheras the weighting and
// score is set in the algorithm
//USE .coerceIn(0.0, 1.0) to make sure that noe value is greater or larger

//Implemented

// Collection (Current weather / LocationForecast)

object TemperatureScorer{
    fun evaluate(temp: Double?, optimalTemp: Double, tolerance: Double = 10.0): Double{
        if (temp == null) return 0.5                    //If no temperature is given, return neutral score
        val diff = abs(optimalTemp-temp)                //Difference between optimal and current temperature
        return (1.0 - (diff / tolerance)).coerceIn(0.0, 1.0)            // Tolerance for temperature where the close
    }
}

object RainScorer{
    fun evaluate(rainNow: Double?, maxTolerableRain: Double = 5.0): Double{
        if (rainNow == null) return 1.0   //Clear weather if no weather is given
        return (1.0 - rainNow / maxTolerableRain).coerceIn(0.0, 1.0)
    }
}

object WindScorer {
    fun evaluate(windSpeed: Double?, threshold: Double = 10.0): Double {
        if (windSpeed == null) return 1.0
        return (1.0 - (windSpeed / threshold)).coerceIn(0.0, 1.0)
    }
}

object HumidityScorer {
    fun evaluate(humidity: Double?, optimalHumidity: Double = 80.0): Double {
        if (humidity == null) return 0.5                    //If no humidity is given, return neutral score
        if (humidity >= optimalHumidity) return 1.0
        return (humidity / optimalHumidity).coerceIn(0.0, 1.0)
    }
}
//object MicroClimateScorer
//Fog and dew


// Growth ( Historical weather / Frost )

//object TempHistoryScorer{}
//object RainHistoryScorer{}
//object HumidityHistoryScorer{}

// Terrain and forest

//object HistoricalFindsScorer{}
//object TreeSpeciesScorer{}
//object DirectionScorer{}
//object GeologyScorer (Ph and scoring of ground from NGU)

object SlopeScorer{
    fun evaluate(slopeDegree: Double?, maxSlopeDegree: Double): Double {
        if (slopeDegree == null) return 0.0
        if (slopeDegree >= maxSlopeDegree) return 0.0
        return (1.0 - (slopeDegree / maxSlopeDegree)).coerceIn(0.0, 1.0)
    }
}




