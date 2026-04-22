package no.uio.ifi.in2000.team16.domain.logic.mushroom

import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile


/*
Basic version of the algorithm, is based on our own research and personal op
Will be the most simple one to run but also the weakest.
The Species profiles has been made using ML algorithm
*/

class BasicAlgorithm : MushroomScorer {
    override suspend fun calculateScore(                    //Overrides the Interface
        context: EnvironmentContext,
        species: SpeciesProfile
    ): EvaluationResult {

        //Main Weighting options
        val weightForest = 45.0
        val weightHistory = 45.0
        val weightCurrent = 10.0
        val weightBonus = 10.0

        //Subweights Forest
        val weightTree = 0.45
        val weightAge = 0.40
        val weightBonitet = 0.15

        //Subweights History
        val weightPastRain = 0.60
        val weightPastTemp = 0.40

        //Subweights Current
        val weightHumidity = 0.20
        val weightRain = 0.30
        val weightWind = 0.10
        val weightMicroClimate = 0.40


        // KILLS - Makes the score 0\
        val killReason =
            SeasonKillSwitch.check(context.currentMonth, species.activeMonths) ?:
            FrostKillSwitch.check(context.weatherHistory?.minTempPast14Days, species.hardinessMinTemp) ?:
            UrbanKillSwitch.check(context.ar5)

        if (killReason != null) {
            return EvaluationResult(0, killReason)
        }

        //Forest
        val treeScore = TreeAffinityScorer.evaluate(
            data = context.forestData,
            prefSpruce = species.spruceAffinity,
            prefPine = species.pineAffinity,
            prefBroadleaf = species.broadleafAffinity
        )
        val ageScore = ForestAgeScorer.evaluate(
            standAge = context.forestData?.standAge,
            optimal = species.optimalForestAge,
            min = species.minForestAge
        )
        val bonitetScore = BonitetScorer.evaluate(
            siteIndex = context.forestData?.siteIndex,
            optimal = species.optimalBonitet
        )

        val forestTotal = (treeScore * weightTree) + (ageScore * weightAge) + (bonitetScore * weightBonitet)

        //Weather History
        val pastRainScore = PastRainScorer.evaluate(
            totalRainPast14Days = context.weatherHistory?.totalRainPast14Days,
            optimalRainMm = species.optimalTotalRain14d
        )
        val pastTempScore = PastTempScorer.evaluate(
            avgTemp = context.weatherHistory?.avgTempPast14Days,
            minTemp = context.weatherHistory?.minTempPast14Days,
            maxTemp = context.weatherHistory?.maxTempPast14Days,
            optimalAvgTemp = species.optimalAvgTemp,
            hardinessMin = species.hardinessMinTemp,
            maxTolerance = species.maxToleranceTemp
        )

        val historyTotal = (pastRainScore * weightPastRain) + (pastTempScore * weightPastTemp)

        //Weather
        val currentRainScore = CurrentRainScorer.evaluate(
            rainNow = context.currentWeather?.rain,
            maxTolerableRain = species.maxCurrentRainMm
        )
        val humidityScore = CurrentHumidityScorer.evaluate(
            humidity = context.currentWeather?.humidity,
            optimalHumidity = species.optimalCurrentHumidity
        )
        val windScore = WindScorer.evaluate(
            windSpeed = context.currentWeather?.windSpeed
        )
        val microClimateScore = MicroClimateScorer.evaluate(
            dewPoint = context.currentWeather?.dewPoint,
            temp = context.currentWeather?.temp,
            fogFraction = context.currentWeather?.fogFraction
        )

        val currentTotal = (currentRainScore * weightRain) + (humidityScore * weightHumidity) + (windScore * weightWind) + (microClimateScore * weightMicroClimate)

        //Total
        var totalScore = (forestTotal * weightForest) + (historyTotal * weightHistory) + (currentTotal * weightCurrent)

        //Bonus
        val findsBoost = HistoricalFindsScorer.evaluate(context.historicalFindCount)
        totalScore += (findsBoost * weightBonus)

        val description = buildDescription(forestTotal, historyTotal, currentTotal)

        return EvaluationResult(
            score = totalScore.toInt().coerceIn(0,100),
            description = description
        )
    }

    private fun buildDescription(forest: Double, history: Double, current: Double): String {
        val parts = mutableListOf<String>()

        if (forest >= 0.8) parts.add("Riktig type skog, alder og bonitet!")
        else if (forest <= 0.4) parts.add("Ikke helt optimal skogsforhold")
        else parts.add("Gode skogforhold.")

        if (history >= 0.8) parts.add("Værhistorikken de siste to ukene har vært perfekt for vekst.")
        else if (history <= 0.4) parts.add("Det har vært litt for dårlig vær i det siste.")
        else parts.add("Vekstvilkårene i det siste har vært greie.")

        if (current >= 0.8) parts.add("I tillegg er det et nydelig sankevær i dag!")
        else if (current <= 0.4) parts.add("Vær obs på at dagens vær gjør sankingen litt røff.")

        if (parts.isEmpty()) {
            return "Forholdene er midt på treet totalt sett." //Pun intended
        }

        return parts.joinToString(" ")
    }
}

