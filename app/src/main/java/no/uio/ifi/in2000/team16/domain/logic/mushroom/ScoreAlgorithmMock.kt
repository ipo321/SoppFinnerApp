package no.uio.ifi.in2000.team16.domain.logic.mushroom

import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile

/*
MOCK algorithm that gives just a random score to test UI functions
*/


class ScoreAlgorithmMock : MushroomScorer {
    override suspend fun calculateScore(                    //Overrides the Interface
        context: EnvironmentContext,
        species: SpeciesProfile
    ): EvaluationResult {

        val randomScore = (0..100).random()                 //Random number between 0 and 100

        return EvaluationResult(                            //Returns the random score and a mock reason
            score = randomScore,
            description = "MOCK DATA: some random reason."
        )
    }
}


