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
            taxKey = "5249504",

            optimalForestAge = 56.0,
            minForestAge = 30.0,
            optimalBonitet = 13.0,

            spruceAffinity = 0.37f,
            pineAffinity = 0.32f,
            broadleafAffinity = 0.21f,

            optimalAvgTemp = 14.5,
            hardinessMinTemp = -1.1,
            maxToleranceTemp = 28.2,
            optimalTotalRain14d = 108.0,

            activeMonths = listOf(6,7,8,9,10,11),
            optimalCurrentHumidity = 80.0,
            maxCurrentRainMm = 15.0,

            minRaw = -0.181f,
            maxRaw = 0.091f
        ),
        SpeciesProfile(
            name = "Steinsopp",
            taxKey = "5954958",

            optimalForestAge = 54.0,
            minForestAge = 28.0,
            optimalBonitet = 13.0,

            spruceAffinity = 0.40f,
            pineAffinity = 0.25f,
            broadleafAffinity = 0.22f,

            optimalAvgTemp = 14.2,
            hardinessMinTemp = -0.4,
            maxToleranceTemp = 27.4,
            optimalTotalRain14d = 98.8,

            activeMonths = listOf(6, 7, 8, 9, 10, 11),
            optimalCurrentHumidity = 80.0,
            maxCurrentRainMm = 15.0,

            minRaw = -0.152f,
            maxRaw = 0.091f
        ),

        SpeciesProfile(
            name = "Skarlagens vårbeger",
            taxKey = "8689016",

            optimalForestAge = 36.0,
            minForestAge = 23.5,
            optimalBonitet = 14.0,

            spruceAffinity = 0.34f,
            pineAffinity = 0.08f,
            broadleafAffinity = 0.55f,

            optimalAvgTemp = 4.6,
            hardinessMinTemp = -10.3,
            maxToleranceTemp = 20.5,
            optimalTotalRain14d = 47.3,

            activeMonths = listOf(3, 4, 5),
            optimalCurrentHumidity = 80.0,
            maxCurrentRainMm = 15.0,

            minRaw = -0.193f,
            maxRaw = 0.085f
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
            commonName = "Skarlagens vårbeger",
            latinName = "Sarcoscypha austriaca",
            imageRes = R.drawable.sarcoscyphaaustriaca,
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Skarlagens vårbeger" }!!
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