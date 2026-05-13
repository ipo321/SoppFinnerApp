package no.uio.ifi.in2000.team16.ui.map

import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext

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
){

    //Object for temporary scorecard information while it loads
    companion object{
        fun empty(score: Int = 0, description: String = "Laster inn data...") = ScoreCardUiState(
            score = score,
            description = description,
            avgTemp = "Laster.." ,
            totalRain = "Laster.." ,
            currentTemp = "Laster..",
            rain = "Laster..",
            humidity = "Laster..",
            windSpeed = "Laster..",
            minTemp = "Laster..",
            maxTemp = "Laster..",
            sprucePercent =  "Laster..",
            pinePercent = "Laster..",
            broadleafPercent = "Laster..",
            fertility = "Laster..",
            dominantTreeSpecies = "Laster..",
            symbolCode = null
        )
    }
}

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
        windSpeed = w?.windSpeed.let{"$it m/s"},
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