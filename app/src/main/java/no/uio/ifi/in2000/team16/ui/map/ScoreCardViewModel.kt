package no.uio.ifi.in2000.team16.ui.map

import android.os.Environment
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import kotlin.Int

/**
 *  ViewModel-component initialized with arguments from the
 *  logic/MushroomScorer data, and connects to the scorecard-view.
 */


class ScoreCardUiState(
    val score: Int,
    val description: String,
    val avgTemp: String,
    val totalRain: String,
    val currentTemp: String,
    val humidity: String,
    val windSpeed: String,
    val rain: String,
    val minTemp: String,
    val maxTemp: String,
    val sprucePercent: String,
    val pinePercent: String,
    val broadleafPercent: String,
    val fertility: String,
    val dominantTreeSpecies: String,
    val symbolCode: String?
)

fun EnvironmentContext.toUiState(score: Int, description: String): ScoreCardUiState{
    val h = weatherHistory
    val w = currentWeather
    val f = forestData

    return ScoreCardUiState(
        score = score,
        description = description,
        avgTemp = h?.avgTempPast14Days?.let {"%.1f°C".format(it)} ?: "--" ,
        totalRain = h?.totalRainPast14Days?.let { "%.1f mm".format(it) } ?: "--" ,
        currentTemp = w?.temp?.let { "${it.toInt()}°C" } ?: "--",
        rain =w?.rain?.let {"$it mm"} ?: "--",
        humidity = w?.humidity?.let {"%.2f%%".format(it)} ?: "--",
        windSpeed = w?.windSpeed.let{"$it m/s"} ?: "--",
        minTemp = h?.minTempPast14Days?.let { "%.1f°C".format(it) } ?: "--",
        maxTemp = h?.maxTempPast14Days?.let { "%.1f°C".format(it) } ?: "--",
        sprucePercent = "${f?.sprucePercentage ?: "--"}%",
        pinePercent = "${f?.pinePercentage ?: "--"}%",
        broadleafPercent = "${f?.broadleafPercentage ?: "--"}%",
        fertility = f?.fertilityCategory ?: "--",
        dominantTreeSpecies = f?.dominantTreeSpecies ?: "--",
        symbolCode = w?.symbolCode
    )
}