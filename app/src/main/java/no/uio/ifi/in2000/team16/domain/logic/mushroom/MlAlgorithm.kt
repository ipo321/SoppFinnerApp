package no.uio.ifi.in2000.team16.domain.logic.mushroom

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OnnxTensor
import android.content.Context
import android.util.Log
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import java.nio.FloatBuffer

class MlAlgorithm(private val contextApp: Context) : MushroomScorer {
    override suspend fun calculateScore(                    //Overrides the Interface
        context: EnvironmentContext,
        species: SpeciesProfile
    ): EvaluationResult {

        //Main Weighting options
        val weightML = 0.90
        val weightCurrent = 10.0
        val weightBonus = 10.0

        // Model range (From training the model)
        val minRaw = -0.193f //Bad place
        val maxRaw = 0.091f //Good place

        //Subweights Current
        val weightHumidity = 0.20
        val weightRain = 0.30
        val weightWind = 0.10
        val weightMicroClimate = 0.40

        // KILLS - Makes the score 0\
        val killReason =
            SeasonKillSwitch.check(context.currentMonth, species.activeMonths) ?:
            UrbanKillSwitch.check(context.ar5)

        if (killReason != null) {
            return EvaluationResult(0, killReason)
        }

        // Data Preparation
        val features = FloatArray(9)
        features[0] = (context.forestData?.sprucePercentage ?: 0).toFloat()
        features[1] = (context.forestData?.pinePercentage ?: 0).toFloat()
        features[2] = (context.forestData?.broadleafPercentage ?: 0).toFloat()
        features[3] = (context.forestData?.standAge ?: 0).toFloat()
        features[4] = (context.weatherHistory?.avgTempPast14Days ?: 0.0).toFloat()
        features[5] = (context.weatherHistory?.minTempPast14Days ?: 0.0).toFloat()
        features[6] = (context.weatherHistory?.maxTempPast14Days ?: 0.0).toFloat()
        features[7] = (context.weatherHistory?.totalRainPast14Days ?: 0.0).toFloat()
        features[8] = (context.forestData?.siteIndex ?: 0).toFloat()

        var mlHabitatScore = 0.0

        // Machine Model
        try {
            val env = OrtEnvironment.getEnvironment()


            //Handling ø/æ/å in model name
            val speciesName = species.name.replace("ø", "o").replace("æ", "ae").replace("å", "a")
            val fileName = "model_${speciesName.replace(" ", "_")}.onnx"

            val modelBytes = contextApp.assets.open(fileName).use { it.readBytes() }
            val session = env.createSession(modelBytes)

            val shape = longArrayOf(1, 9)
            val tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(features), shape)

            val inputName = session.inputNames.iterator().next()
            val result = session.run(mapOf(inputName to tensor))

            val outputScores = result[1].value as Array<FloatArray>
            val rawScore = outputScores[0][0]

            //Score between 0 and 100
            val percentage = (rawScore - minRaw) / (maxRaw - minRaw)
            mlHabitatScore = (percentage * 100).toDouble().coerceIn(0.0, 100.0)

            tensor.close()
            result.close()
            session.close()

        } catch (e: Exception) {
            Log.e("T16_MlAlgorithm", "Failed to run ONNX for ${species.name}",e)
            return EvaluationResult(0, "Mangler model for soppen.")
        }

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

        //Total result
        var totalScore = (mlHabitatScore * weightML) + (currentTotal * weightCurrent)

        //Bonus
        val findsBoost = HistoricalFindsScorer.evaluate(context.historicalFindCount)
        totalScore += (findsBoost * weightBonus)

        val description = buildDescription(mlHabitatScore, currentTotal)


        return EvaluationResult(
            score = totalScore.toInt().coerceIn(0, 100),
            description = description
        )
    }


    private fun buildDescription(ml: Double, current: Double): String {
        val habitatText = if (ml > 75) {
            "Området er optimalt!"
        } else if (ml > 40) {
            "Området har noe potensiale, men er ikke perfekt."
        } else {
            "Området har ikke noe potensiale."
        }

        val weatherText = if (current > 0.7) {
            "I tillegg er det strålende sankevær i dag."
        } else if (current < 0.4) {
            "Vær obs på at dagens vær gjør sankingen litt røff."
        } else {
            ""
        }

        return "$habitatText $weatherText".trim()
    }
}

