package no.uio.ifi.in2000.team16.domain.logic.mushroom

import no.uio.ifi.in2000.team16.domain.model.ForestData
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow

/*
Scorers are used to build up the algorithm. They can be combined in different ways to create different types. They will all return a value between 0.0 and 1.0 wheres the weighting and
score is set in the algorithm. If you wanna design new ones: USE .coerceIn(0.0, 1.0) to make sure that no value is greater or larger than 1.0 or smaller than 0.0
*/

// Helper function
fun gaussianScore(current: Double, optimal: Double, tolerance: Double): Double {
    if (tolerance <= 0.0) return if (current == optimal) 1.0 else 0.0
    // Sigma should be around 13% when the deviation is around tolerance
    val sigma = tolerance / 2.0
    val exponent = -(current - optimal).pow(2) / (2 * sigma.pow(2))
    return exp(exponent).coerceIn(0.0, 1.0)
}


// Terrain and forest

object BonitetScorer {
    fun evaluate(siteIndex: Int?, optimal: Double): Double {
        if (siteIndex == null) return 0.5
        // Tolerance set to 8 as that reflects a large difference in H40 points
        return gaussianScore(siteIndex.toDouble(), optimal, 8.0)
    }
}

object TreeAffinityScorer {
    fun evaluate(data: ForestData?, prefSpruce: Float, prefPine: Float, prefBroadleaf: Float): Double {
        // Neutral score if no data is available
        if (data == null || (data.sprucePercentage == null && data.pinePercentage == null)) return 0.5

        // Convert from percentage to fraction
        val actualSpruce = (data.sprucePercentage ?: 0) / 100.0
        val actualPine = (data.pinePercentage ?: 0) / 100.0
        val actualBroadleaf = (data.broadleafPercentage ?: 0) / 100.0

        // Ideal fractions based on preferences
        val totalPref = prefSpruce + prefPine + prefBroadleaf
        if (totalPref == 0f) return 0.0

        val idealSpruce = prefSpruce / totalPref
        val idealPine = prefPine / totalPref
        val idealBroadleaf = prefBroadleaf / totalPref

        // Similarity Index
        val diffSpruce = abs(actualSpruce - idealSpruce)
        val diffPine = abs(actualPine - idealPine)
        val diffBroadleaf = abs(actualBroadleaf - idealBroadleaf)

        // Max difference of 1.0 (From 2 to 1)
        val similarity = 1.0 - ((diffSpruce + diffPine + diffBroadleaf) / 2.0)

        return similarity.coerceIn(0.0, 1.0)
    }
}

object ForestAgeScorer {
    fun evaluate(standAge: Int?, optimal: Double, min: Double): Double {
        if (standAge == null) return 0.5
        val age = standAge.toDouble()
        if (age >= optimal) return 1.0  //If older than optimal return max score
        val tolerance = optimal - min   //Difference between optimal and min
        return gaussianScore(age, optimal, tolerance)
    }
}

// Mushroom findings
object HistoricalFindsScorer{
    fun evaluate(historicalFinds: Int, sufficientFinds: Int = 2): Double {
        if (historicalFinds >= sufficientFinds) return 1.0
        return (historicalFinds.toDouble() / sufficientFinds).coerceIn(0.0, 1.0)
    }
}

// Weather History
object PastRainScorer{
    fun evaluate(totalRainPast14Days: Double?, optimalRainMm: Double): Double{
        if (totalRainPast14Days == null) return 0.2   //Clear weather if no weather is given
        if (totalRainPast14Days >= optimalRainMm) return 1.0
        val tolerance = optimalRainMm * 0.7 // Accept some tolerance
        return gaussianScore(totalRainPast14Days, optimalRainMm, tolerance)
    }
}


object PastTempScorer {
    fun evaluate(avgTemp: Double?, minTemp: Double?, maxTemp: Double?, optimalAvgTemp: Double,
                 hardinessMin: Double, maxTolerance: Double): Double {
        if (avgTemp == null) return 0.5
        //Can be both to hot and to cold
        if (maxTemp != null && maxTemp > maxTolerance) return 0.0
        if (minTemp != null && minTemp < hardinessMin) return 0.0

        val tolerance = if (avgTemp < optimalAvgTemp) {
            optimalAvgTemp - hardinessMin
        } else {
            maxTolerance - optimalAvgTemp
        }
        return gaussianScore(avgTemp, optimalAvgTemp, tolerance)
    }
}

// Weather Current
object CurrentRainScorer{
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

object CurrentHumidityScorer {
    fun evaluate(humidity: Double?, optimalHumidity: Double = 80.0): Double {
        if (humidity == null) return 0.5                    //If no humidity is given, return neutral score
        if (humidity >= optimalHumidity) return 1.0

        val tolerance = optimalHumidity * 0.5
        return gaussianScore(humidity, optimalHumidity, tolerance)
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




