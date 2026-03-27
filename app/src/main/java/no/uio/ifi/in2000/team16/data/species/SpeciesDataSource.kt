package no.uio.ifi.in2000.team16.data.species

import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SpeciesDataSource @Inject constructor(){

    //keeps track of how many mushrooms we have profiled easily,
    //gets updated to actual value when getAllSpecies is called.
    var amount: Int = 0

    private val allSpecies = listOf(
        SpeciesProfile(
            name = "Kantarell",
            type = 1,
            activeMonths = listOf(6, 7, 8, 9, 10),
            killTemp = -2.0,
            optimalTemp = 18.0,
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

//this file holds the data classes and the acual value that the MushroominfoScreen uses
//add MushroomDisplay to allMushrooms for additional Mushroom screens

    data class MushroomProperty(
        val label: String,
        val iconRes: Int,
        val isNegative: Boolean = false  // was isRed
    )

    data class MushroomInfo(
        val commonName: String,
        val latinName: String,
        val imageRes: Int,
        val properties: List<MushroomProperty>,
        val speciesProfile: SpeciesProfile?
    )

    val allMushrooms = listOf(
        MushroomInfo(
            commonName = "Kantarell",
            latinName = "Cantharellus cibarius",
            imageRes = R.drawable.chanterelle,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            speciesProfile = allSpecies.find{it.name == "Kantarell"}

        ),
        MushroomInfo(
            commonName = "Steinsopp",
            latinName = "Boletus edulis",
            imageRes = R.drawable.rockfungus,
            properties = listOf(
                MushroomProperty("Giftig", R.drawable.skull, isNegative = true),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            speciesProfile = allSpecies.find{it.name == "Steinsopp"}
        ),

    )



    fun getAllSpecies(): List<SpeciesProfile> {
        //keeps track of how many mushrooms we have profiled easily
        amount = allSpecies.count()
        return allSpecies
    }

    fun getAllMushroomInfos(): List<MushroomInfo> {

        return allMushrooms
    }
}