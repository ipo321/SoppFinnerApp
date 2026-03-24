package no.uio.ifi.in2000.team16.domain.logic.mushroom

import no.uio.ifi.in2000.team16.domain.model.ForestData
import kotlin.math.abs

/*
Scorers are used to build up the algorithm. They can be combined in different ways to create different types. They will all return a value between 0.0 and 1.0 wheres the weighting and
score is set in the algorithm. If you wanna design new ones: USE .coerceIn(0.0, 1.0) to make sure that no value is greater or larger than 1.0 or smaller than 0.0
*/


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

object MicroClimateScorer {
    fun evaluate(dewPoint: Double?, temp: Double?, fogFraction: Double?): Double {
        var score = 0.0
        var conditions = 0

        if (temp != null && dewPoint != null) { //temp and dewPoint to calculate microclimate
            val diff = abs(temp - dewPoint)
            score += (1.0 - (diff / 10.0)).coerceIn(0.0, 1.0)
            conditions++
        }
        if (fogFraction != null) {  //add fog fraction, 50+ is positive
            score += (fogFraction / 50.0).coerceIn(0.0, 1.0)
            conditions++
        }
        if (conditions == 0) return 0.5   //If no conditions are given, return neutral score
        return (score / conditions).coerceIn(0.0, 1.0)
    }
}


// Growth ( Historical weather / Frost )

object PastRainScorer{
    fun evaluate(totalRainPast14Days: Double?, optimalRainMm: Double = 5.0): Double{
        if (totalRainPast14Days == null) return 0.0   //Clear weather if no weather is given
        if (totalRainPast14Days >= optimalRainMm) return 1.0
        return (totalRainPast14Days / optimalRainMm).coerceIn(0.0, 1.0)
    }
}

object PastTempScorer{
    fun evaluate(avgTempPast14Days: Double?, optimalTemp: Double, tolerance: Double = 10.0): Double{
        if (avgTempPast14Days == null) return 0.0
        val diff = abs(optimalTemp-avgTempPast14Days)
        return (1.0 - (diff / tolerance)).coerceIn(0.0, 1.0)
    }
}

// Terrain and forest

object HistoricalFindsScorer{
    fun evaluate(historicalFinds: Int, sufficientFinds: Int = 2): Double {
        if (historicalFinds >= sufficientFinds) return 1.0
        return (historicalFinds.toDouble() / sufficientFinds).coerceIn(0.0, 1.0)
    }
}

object TreeSpeciesScorer{
    fun evaluate(treeSpecies: Int?, preferredSpecies: List<Int>): Double {
        if (treeSpecies == null) return 0.0
        if (treeSpecies in preferredSpecies) return 1.0
        return 0.0
    }
}

object SlopeScorer{
    fun evaluate(slopeDegree: Double?, maxSlopeDegree: Double): Double {
        if (slopeDegree == null) return 0.0
        if (slopeDegree >= maxSlopeDegree) return 0.0
        return (1.0 - (slopeDegree / maxSlopeDegree)).coerceIn(0.0, 1.0)
    }
}

object TreePercentageScorer {
    fun evaluate(data: ForestData?, preferred: List<String>): Double {
        if (data == null) return 0.5

        if (data.sprucePercentage != null || data.pinePercentage != null || data.broadleafPercentage != null) {
            var pct = 0
            if (preferred.any { it.contains("Gran", true) }) pct += data.sprucePercentage ?: 0
            if (preferred.any { it.contains("Furu", true) }) pct += data.pinePercentage ?: 0
            if (preferred.any { it.contains("Lauv", true) }) pct += data.broadleafPercentage ?: 0
            return (pct / 100.0).coerceIn(0.0, 1.0)
        }

        val dominant = data.dominantTreeSpecies ?: return 0.5
        return if (preferred.any { dominant.contains(it, true) }) 1.0 else 0.0
    }
}

object ForestAgeScorer {
    fun evaluate(standAge: Int?, minAge: Int): Double {
        if (standAge == null) return 0.5
        if (standAge >= minAge) return 1.0

        return (standAge.toDouble() / minAge).coerceIn(0.0, 1.0)
    }
}


//Can be implemented:
//PH /ground scorer
//Direction scorer

