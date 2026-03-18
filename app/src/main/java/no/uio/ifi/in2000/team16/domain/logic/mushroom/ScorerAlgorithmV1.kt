package no.uio.ifi.in2000.team16.domain.logic.mushroom

import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile


//MOCK algorithm that gives just a random score to test UI functions

class MushroomScorerV1 : MushroomScorer {
    override fun calculateScore(                    //Overrides the Interface
        context: EnvironmentContext,
        species: SpeciesProfile
    ): EvaluationResult {
        val killReason =
            SeasonKillSwitch.check(context.currentMonth, species.activeMonths)
            ?: FrostKillSwitch.check(context.currentWeather?.temp, species.killTemp)
            ?: UrbanKillSwitch.check(context.forestData?.arType)

        if (killReason != null) {
            return EvaluationResult(0, killReason)
        }

        val tempRaw = TemperatureScorer.evaluate(
            temp = context.currentWeather?.temp,
            optimalTemp = species.optimalTemp,
            tolerance = species.tempToleranceC
        )

        val rainRaw = RainScorer.evaluate(
            rainNow = context.currentWeather?.rain,
            maxTolerableRain = species.maxCurrentRainMm
        )

        val tempWeight = 50.0
        val rainWeight = 50.0

        val totalScore = (tempRaw * tempWeight) + (rainRaw * rainWeight)

        return EvaluationResult(
            score = totalScore.toInt().coerceIn(0,100),
            description = "V1 MVP, temp and rain"
        )
    }
}


