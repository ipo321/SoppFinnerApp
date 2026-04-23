package no.uio.ifi.in2000.team16.domain.logic.mushroom

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScorerProvider @Inject constructor(
    private val aiAlgorithmV1: AiAlgorithmV1
) {

    var activeScorer: MushroomScorer = MushroomScorerV2()
        private set

    var activeHeatScorer: MushroomScorer = HeatAlgorithmV1()
        private set

    fun setAlgorithm(type: AlgorithmType) {
        activeScorer = when (type) {
            AlgorithmType.V1 -> MushroomScorerV1()
            AlgorithmType.V2 -> MushroomScorerV2()
            AlgorithmType.MOCK -> ScoreAlgorithmMock()
            AlgorithmType.HEAT1 -> HeatAlgorithmV1()
            AlgorithmType.AI1 -> aiAlgorithmV1
        }
    }
    fun setHeatAlgorithm(type: AlgorithmType) {
        activeHeatScorer = when (type) {
            AlgorithmType.V1 -> MushroomScorerV1()
            AlgorithmType.V2 -> MushroomScorerV2()
            AlgorithmType.MOCK -> ScoreAlgorithmMock()
            AlgorithmType.HEAT1 -> HeatAlgorithmV1()
            AlgorithmType.AI1 -> aiAlgorithmV1
        }
    }
}

enum class AlgorithmType {
    V1, V2, MOCK, HEAT1, AI1
}