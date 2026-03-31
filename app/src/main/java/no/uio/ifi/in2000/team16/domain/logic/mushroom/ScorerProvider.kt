package no.uio.ifi.in2000.team16.domain.logic.mushroom

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScorerProvider @Inject constructor() {

    var activeScorer: MushroomScorer = MushroomScorerV2()
        private set

    var activeHeatScorer: MushroomScorer = MushroomScorerV2()
        private set

    fun setAlgorithm(type: AlgorithmType) {
        activeScorer = when (type) {
            AlgorithmType.V1 -> MushroomScorerV1()
            AlgorithmType.V2 -> MushroomScorerV2()
            AlgorithmType.MOCK -> ScoreAlgorithmMock()
        }
    }
    fun setHeatAlgorithm(type: AlgorithmType) {
        activeHeatScorer = when (type) {
            AlgorithmType.V1 -> MushroomScorerV1()
            AlgorithmType.V2 -> MushroomScorerV2()
            AlgorithmType.MOCK -> ScoreAlgorithmMock()
        }
    }
}

enum class AlgorithmType {
    V1, V2, MOCK
}