package no.uio.ifi.in2000.team16.domain.logic.mushroom

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtSession
import android.content.Context
import android.util.Log
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import java.nio.FloatBuffer
import java.util.concurrent.ConcurrentHashMap

class MlAlgorithm(private val contextApp: Context) : MushroomScorer {

// Caching
companion object {
    private val env by lazy { OrtEnvironment.getEnvironment() }
    private val sessionCache = ConcurrentHashMap<String, OrtSession>()

    private fun getSession(context: Context, speciesName: String): OrtSession {
        val name = speciesName.replace("ø", "o").replace("æ", "ae").replace("å", "a").replace(" ", "_")
        val fileName = "model_$name.onnx"
        return sessionCache.getOrPut(fileName) {
            Log.i("T16_MlAlgorithm", "Loading $fileName to memory and caching...")
            val modelBytes = context.assets.open(fileName).readBytes()
            env.createSession(modelBytes)
        }
    }
}




    override suspend fun calculateScore(                    //Overrides the Interface
        context: EnvironmentContext,
        species: SpeciesProfile
    ): EvaluationResult {
        Log.i("T16_MlAlgorithm", "Running ML algorithm for ${species.name}")
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

        // Machine Model
        val mlHabitatScore: Double = try {
            val session = getSession(contextApp, species.name)

            val shape = longArrayOf(1, 9)
            val tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(features), shape)

            val inputName = session.inputNames.iterator().next()
            val result = session.run(mapOf(inputName to tensor))

            @Suppress("UNCHECKED_CAST")
            val outputScores = result[1].value as Array<FloatArray>
            val rawScore = outputScores[0][0]

            //Score between 0 and 100
            val percentage = (rawScore - minRaw) / (maxRaw - minRaw)

            tensor.close()
            result.close()

            (percentage * 100).toDouble().coerceIn(0.0, 100.0)
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

        Log.i("T16_MlAlgorithm", "Finished ML algorithm for ${species.name}")

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
        } else if (current < 0.2) { // This was earlier 0.4. Changed it to 0.2 to not get the messages with quite low wind.
            "Vær obs på at dagens vær gjør sankingen litt røff."
        } else {
            ""
        }

        return "$habitatText $weatherText".trim()
    }
}

