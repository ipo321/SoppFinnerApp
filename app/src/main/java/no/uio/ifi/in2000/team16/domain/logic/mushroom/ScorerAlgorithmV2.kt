package no.uio.ifi.in2000.team16.domain.logic.mushroom

import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile


/*
Second version of the algorithm, it has the most important kill switches and the scorers for the current weather
*/

class MushroomScorerV2 : MushroomScorer {
    override suspend fun calculateScore(                    //Overrides the Interface
        context: EnvironmentContext,
        species: SpeciesProfile
    ): EvaluationResult {


        //KILLS
        val killReason =
            SeasonKillSwitch.check(context.currentMonth, species.activeMonths)
                ?: FrostKillSwitch.check(context.currentWeather?.temp, species.killTemp)
                ?: UrbanKillSwitch.check(context.ar5)

        if (killReason != null) {
            return EvaluationResult(0, killReason)
        }

        //Forest
        val treeScore = TreePercentageScorer.evaluate(
            data = context.forestData,
            preferred = species.preferredTreeSpecies
        )
        val ageScore = ForestAgeScorer.evaluate(
            context.forestData?.standAge,
            species.minForestAge
        )
        val forestTotal = (treeScore * 0.6) + (ageScore * 0.4)

        //Weather History
        val pastRainScore = PastRainScorer.evaluate(
            totalRainPast14Days = context.weatherHistory?.totalRainPast14Days ?:  0.0,
            optimalRainMm = species.optimalRainMm
        )

        val pastTempScore = PastTempScorer.evaluate(
            avgTempPast14Days = context.weatherHistory?.avgTempPast14Days ?: 15.0,
            optimalTemp = species.optimalTemp,
            tolerance = species.tempToleranceC
        )
        val historyTotal = (pastRainScore * 0.6) + (pastTempScore * 0.4)

        //Weather
        val currentTempScore = TemperatureScorer.evaluate(context.currentWeather?.temp, species.optimalTemp, species.tempToleranceC)
        val currentRainScore = RainScorer.evaluate(context.currentWeather?.rain, species.maxCurrentRainMm)
        val humidityScore = HumidityScorer.evaluate(context.currentWeather?.humidity, species.optimalHumidity)

        val currentTotal = (currentTempScore * 0.4) + (currentRainScore * 0.4) + (humidityScore * 0.2)

        //Total
        var totalScore = (forestTotal * 40.0) + (historyTotal * 30.0) + (currentTotal * 30.0)


        //Bonus
        val findsBoost = HistoricalFindsScorer.evaluate(context.historicalFindCount)
        totalScore += (findsBoost * 10.0)

        val description = buildDescription(forestTotal, historyTotal, currentTotal)

        return EvaluationResult(
            score = totalScore.toInt().coerceIn(0,100),
            description = description
        )
    }

    private fun buildDescription(forest: Double, history: Double, current: Double): String {
        val parts = mutableListOf<String>()

        if (forest >= 0.8) parts.add("Perfekt skogtype!")
        else if (forest <= 0.3) parts.add("Feil trær eller for ung skog.")
        else parts.add("Helt ok skogforhold.")

        if (history >= 0.8) parts.add("Været de siste ukene har vært optimalt for vekst.")
        else if (history <= 0.4) parts.add("Det har vært for tørt eller feil temperatur i det siste.")
        else parts.add("Grei veksthistorikk.")

        if (current >= 0.8) parts.add("Flott sankevær i dag!")
        else if (current <= 0.4) parts.add("Kanskje ikke det beste været å plukke i akkurat nå.")

        if (parts.isEmpty()) {
            return "Helt greie forhold totalt sett."
        }

        return parts.joinToString(" ")
    }
}

