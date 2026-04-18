package no.uio.ifi.in2000.team16.domain.logic.mushroom

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScorerProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val aiAlgorithm: AiAlgorithm
) {

    var activeScorer: MushroomScorer = BasicAlgorithm()
        private set

    var activeHeatScorer: MushroomScorer = BasicAlgorithm()
        private set

    fun setAlgorithm(type: AlgorithmType) {
        activeScorer = when (type) {
            AlgorithmType.BASIC -> BasicAlgorithm()
            AlgorithmType.ML -> MlAlgorithm(context)
            AlgorithmType.AI -> aiAlgorithm
        }
    }
    fun setHeatAlgorithm(type: AlgorithmType) {
        activeHeatScorer = when (type) {
            AlgorithmType.BASIC -> BasicAlgorithm()
            AlgorithmType.ML -> MlAlgorithm(context)
            AlgorithmType.AI -> aiAlgorithm
        }
    }
}

enum class AlgorithmType {
    BASIC, ML, AI
}