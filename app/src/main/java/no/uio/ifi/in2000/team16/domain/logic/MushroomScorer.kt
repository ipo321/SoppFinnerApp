package no.uio.ifi.in2000.team16.domain.logic

import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile


//Interface to be implemented by the algorithm

interface MushroomScorer {
    fun calculateScore(context: EnvironmentContext, species: SpeciesProfile): EvaluationResult
}


//To use in your ViewModel, use
// val scorer: MushroomScorer = MockMushroomScorer()
// To later update to new algorithm change MockMushroomScorer with the designated algorithm
// Return values should be the same because of the interface

//MockData:
//In your code use scorer.calculateScore(EnvironmentContext.Mock, SpeciesProfile.Mock)
