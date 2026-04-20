package no.uio.ifi.in2000.team16.data.species

import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.domain.model.MushroomProperty
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SpeciesDataSource @Inject constructor(){

    //keeps track of how many mushrooms we have profiled easily,
    //gets updated to actual value when getAllSpecies is called.
    var amount: Int = 0


    val allSpeciesProfiles = listOf(
        SpeciesProfile(
            name = "Kantarell",
            type = 1,
            taxKey = "5249504",
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
            type = 1,
            taxKey = "5954958",
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
            type = 1,
            taxKey = "2594939",
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

    private val allMushrooms = listOf(
        Mushroom(
            commonName = "Kantarell",
            latinName = "Cantharellus cibarius",
            imageRes = R.drawable.cantharelluscibarius,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Kantarell" }!!
        ),
        Mushroom(
            commonName = "Steinsopp",
            latinName = "Boletus edulis",
            imageRes = R.drawable.boletusreticulatus,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Steinsopp" }!!
        ),
        Mushroom(
            commonName = "Skarlagen vårbeger",
            latinName = "Sarcoscypha austriaca",
            imageRes = R.drawable.sarcoscyphaaustriaca,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Skarlagen vårbeger" }!!
        ),
        Mushroom(
            commonName = "Traktkantarel",
            latinName = "Craterellus tubaeformis",
            imageRes = R.drawable.funnelchanterell,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
           profile = allSpeciesProfiles.find { it.name == "Kantarell" }!!
        ),
        Mushroom(
            commonName = "Blek piggsopp",
            latinName = "Hydnum repandum",
            imageRes = R.drawable.hedgehog,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Kantarell" }!!
        ),
        Mushroom(
            commonName = "Fåresopp",
            latinName = "albatrellus ovinus",
            imageRes = R.drawable.sheepfungus,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Kantarell" }!!
        ),
        Mushroom(
            commonName = "Granmatriske",
            latinName = "Lactarius deterrimus",
            imageRes = R.drawable.gramatriske,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Kantarell" }!!
        ),
        Mushroom(
            commonName = "Rødskrubb",
            latinName = "Leccinum versipelle",
            imageRes = R.drawable.versipelle,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Kantarell" }!!
        ),
        Mushroom(
            commonName = "Brunskrubb",
            latinName = "Leccinum scabrum",
            imageRes = R.drawable.scabrum,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Kantarell" }!!
        ),
        Mushroom(
            commonName = "Matblekksopp",
            latinName = "Coprinus comatus",
            imageRes = R.drawable.coprinuscomatus,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Kantarell" }!!
        ),
        Mushroom(
            commonName = "Svart trompetsopp",
            latinName = "Craterellus cornucopioides",
            imageRes = R.drawable.craterelluscornucopioides,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Kantarell" }!!
        )

    )

    fun getAllMushrooms(): List<Mushroom> {
        amount = allMushrooms.size
        return allMushrooms
    }

    fun getAmountOfMushrooms(): Int {
        return amount
    }
}