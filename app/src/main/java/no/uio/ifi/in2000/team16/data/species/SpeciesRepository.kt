package no.uio.ifi.in2000.team16.data.species

import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeciesRepository @Inject constructor(){
    private val allSpecies = listOf(
        SpeciesProfile(
            name = "Kantarell",
            type = 1,
            activeMonths = listOf(6, 7, 8, 9, 10),
            killTemp = -2.0,
            optimalTemp = 15.0,
            tempToleranceC = 8.0,

            optimalRainMm = 20.0,
            maxCurrentRainMm = 5.0,
            optimalHumidity = 80.0,

            preferredTreeSpecies = listOf("Gran", "Furu"),
            minForestAge = 10,

            preferredTerrain = listOf(1, 2),
            maxSlope = 35.0,
            windPenaltyThreshold = 8.0
        ),
        SpeciesProfile(
            name = "Steinsopp",
            type = 2,
            activeMonths = listOf(8, 9, 10), //Later in season
            killTemp = 0.0,
            optimalTemp = 13.0,
            tempToleranceC = 6.0,

            optimalRainMm = 25.0,  // Likes moist ground
            maxCurrentRainMm = 10.0,
            optimalHumidity = 85.0,

            preferredTreeSpecies = listOf("Gran", "Furu"),
            minForestAge = 10,

            preferredTerrain = listOf(1, 3),
            maxSlope = 25.0,
            windPenaltyThreshold = 6.0
        ),
        SpeciesProfile(
            name = "Skarlagen vårbeger",
            type = 3,
            activeMonths = listOf(3, 4, 5),
            killTemp = -8.0,
            optimalTemp = 5.0,
            tempToleranceC = 7.0,

            optimalRainMm = 15.0,
            maxCurrentRainMm = 15.0,
            optimalHumidity = 90.0,

            preferredTreeSpecies = listOf("Lauv"),
            minForestAge = 15,

            preferredTerrain = listOf(1, 4),
            maxSlope = 20.0,
            windPenaltyThreshold = 5.0
        )
    )

    fun getSpeciesByName(name: String): SpeciesProfile?{
        return allSpecies.find {it.name.equals(name,ignoreCase = true)}
    }

    fun getAllSpecies(): List<SpeciesProfile> {
        return allSpecies
    }
}