package no.uio.ifi.in2000.team16.data.species

import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.domain.model.MushroomProperty
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import javax.inject.Inject
import javax.inject.Singleton

interface SpeciesDataSourceInterface {
    var amount: Int
    val mushroomList: List<Mushroom>

    fun getAllMushrooms(): List<Mushroom>

    fun getAmountOfMushrooms(): Int
}

@Singleton
class SpeciesDataSource @Inject constructor() : SpeciesDataSourceInterface {

    //keeps track of how many mushrooms we have profiled easily,
    //gets updated to actual value when getAllSpecies is called.
    override var amount: Int = 0


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
        ),
        SpeciesProfile(
            name = "Traktkantarell",
            taxKey = "2554536",
            optimalForestAge = 59.0,
            minForestAge = 34.0,
            optimalBonitet = 13.0,
            spruceAffinity = 0.46f,
            pineAffinity = 0.31f,
            broadleafAffinity = 0.15f,
            optimalAvgTemp = 12.5,
            hardinessMinTemp = -2.6,
            maxToleranceTemp = 26.8,
            optimalTotalRain14d = 102.5,
            activeMonths = listOf(8, 9, 10, 11),
            optimalCurrentHumidity = 85.0,
            maxCurrentRainMm = 15.0,
            minRaw = -0.158f,
            maxRaw = 0.089f
        ),
        SpeciesProfile(
            name = "Blek piggsopp",
            taxKey = "2554716",
            optimalForestAge = 60.0,
            minForestAge = 34.0,
            optimalBonitet = 13.0,
            spruceAffinity = 0.43f,
            pineAffinity = 0.27f,
            broadleafAffinity = 0.18f,
            optimalAvgTemp = 12.8,
            hardinessMinTemp = -1.9,
            maxToleranceTemp = 27.8,
            optimalTotalRain14d = 90.3,
            activeMonths = listOf(7, 8, 9, 10),
            optimalCurrentHumidity = 80.0,
            maxCurrentRainMm = 15.0,
            minRaw = -0.140f,
            maxRaw = 0.082f
        ),
        SpeciesProfile(
            name = "Fåresopp",
            taxKey = "2551823",
            optimalForestAge = 71.0,
            minForestAge = 34.7,
            optimalBonitet = 13.0,
            spruceAffinity = 0.59f,
            pineAffinity = 0.27f,
            broadleafAffinity = 0.09f,
            optimalAvgTemp = 13.6,
            hardinessMinTemp = -2.0,
            maxToleranceTemp = 28.3,
            optimalTotalRain14d = 65.1,
            activeMonths = listOf(8, 9, 10),
            optimalCurrentHumidity = 75.0,
            maxCurrentRainMm = 10.0,
            minRaw = -0.144f,
            maxRaw = 0.082f
        ),
        SpeciesProfile(
            name = "Granmatriske",
            taxKey = "7925734",
            optimalForestAge = 52.0,
            minForestAge = 24.0,
            optimalBonitet = 13.0,
            spruceAffinity = 0.51f,
            pineAffinity = 0.19f,
            broadleafAffinity = 0.20f,
            optimalAvgTemp = 13.7,
            hardinessMinTemp = -0.3,
            maxToleranceTemp = 28.3,
            optimalTotalRain14d = 89.2,
            activeMonths = listOf(7, 8, 9, 10),
            optimalCurrentHumidity = 80.0,
            maxCurrentRainMm = 15.0,
            minRaw = -0.189f,
            maxRaw = 0.089f
        ),
        SpeciesProfile(
            name = "Rødskrubb",
            taxKey = "2524490",
            optimalForestAge = 56.0,
            minForestAge = 27.0,
            optimalBonitet = 13.0,
            spruceAffinity = 0.32f,
            pineAffinity = 0.27f,
            broadleafAffinity = 0.28f,
            optimalAvgTemp = 13.9,
            hardinessMinTemp = -0.6,
            maxToleranceTemp = 28.4,
            optimalTotalRain14d = 87.5,
            activeMonths = listOf(6, 7, 8, 9, 10),
            optimalCurrentHumidity = 75.0,
            maxCurrentRainMm = 15.0,
            minRaw = -0.136f,
            maxRaw = 0.082f
        ),
        SpeciesProfile(
            name = "Brunskrubb",
            taxKey = "9141390",
            optimalForestAge = 56.0,
            minForestAge = 28.0,
            optimalBonitet = 13.0,
            spruceAffinity = 0.32f,
            pineAffinity = 0.25f,
            broadleafAffinity = 0.29f,
            optimalAvgTemp = 14.3,
            hardinessMinTemp = -0.1,
            maxToleranceTemp = 28.0,
            optimalTotalRain14d = 86.5,
            activeMonths = listOf(6, 7, 8, 9, 10),
            optimalCurrentHumidity = 75.0,
            maxCurrentRainMm = 15.0,
            minRaw = -0.166f,
            maxRaw = 0.082f
        ),
        SpeciesProfile(
            name = "Matblekksopp",
            taxKey = "7987658",
            optimalForestAge = 48.0,
            minForestAge = 25.0,
            optimalBonitet = 13.0,
            spruceAffinity = 0.32f,
            pineAffinity = 0.22f,
            broadleafAffinity = 0.39f,
            optimalAvgTemp = 11.6,
            hardinessMinTemp = -1.9,
            maxToleranceTemp = 26.8,
            optimalTotalRain14d = 73.9,
            activeMonths = listOf(5, 6, 7, 8, 9, 10),
            optimalCurrentHumidity = 80.0,
            maxCurrentRainMm = 10.0,
            minRaw = -0.150f,
            maxRaw = 0.089f
        ),
        SpeciesProfile(
            name = "Svart trompetsopp",
            taxKey = "2554662",
            optimalForestAge = 58.0,
            minForestAge = 33.0,
            optimalBonitet = 13.0,
            spruceAffinity = 0.30f,
            pineAffinity = 0.29f,
            broadleafAffinity = 0.28f,
            optimalAvgTemp = 13.5,
            hardinessMinTemp = -1.6,
            maxToleranceTemp = 27.3,
            optimalTotalRain14d = 103.6,
            activeMonths = listOf(8, 9, 10),
            optimalCurrentHumidity = 85.0,
            maxCurrentRainMm = 15.0,
            minRaw = -0.176f,
            maxRaw = 0.064f
        )
    )

    override val mushroomList = listOf(
        Mushroom(
            commonName = "Kantarell",
            latinName = "Cantharellus cibarius",
            imageRes = R.drawable.cantharelluscibarius,
            bioRes = R.string.kantarell_bio.toString(),
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
            bioRes = R.string.steinsopp_bio.toString(),
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
            bioRes = R.string.skarlagen_vårbeger_bio.toString(),
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Skarlagens vårbeger" }!!
        ),
        Mushroom(
            commonName = "Traktkantarell",
            latinName = "Craterellus tubaeformis",
            imageRes = R.drawable.funnelchanterell,
            bioRes = R.string.traktantarell_bio.toString(),
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
           profile = allSpeciesProfiles.find { it.name == "Traktkantarell" }!!
        ),
        Mushroom(
            commonName = "Blek piggsopp",
            latinName = "Hydnum repandum",
            imageRes = R.drawable.hedgehog,
            bioRes = R.string.blek_piggsopp_bio.toString(),
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Blek piggsopp" }!!
        ),
        Mushroom(
            commonName = "Fåresopp",
            latinName = "albatrellus ovinus",
            imageRes = R.drawable.sheepfungus,
            bioRes = R.string.fåresopp_bio.toString(),
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Fåresopp" }!!
        ),
        Mushroom(
            commonName = "Granmatriske",
            latinName = "Lactarius deterrimus",
            imageRes = R.drawable.gramatriske,
            bioRes = R.string.granmatriske_bio.toString(),
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Granmatriske" }!!
        ),
        Mushroom(
            commonName = "Rødskrubb",
            latinName = "Leccinum versipelle",
            imageRes = R.drawable.versipelle,
            bioRes = R.string.rødskrubb_bio.toString(),
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Rødskrubb" }!!
        ),
        Mushroom(
            commonName = "Brunskrubb",
            latinName = "Leccinum scabrum",
            imageRes = R.drawable.scabrum,
            bioRes = R.string.brunskrubb_bio.toString(),
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Brunskrubb" }!!
        ),
        Mushroom(
            commonName = "Matblekksopp",
            latinName = "Coprinus comatus",
            imageRes = R.drawable.coprinuscomatus,
            bioRes = R.string.matblekksopp.toString(),
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Matblekksopp" }!!
        ),
        Mushroom(
            commonName = "Svart trompetsopp",
            latinName = "Craterellus cornucopioides",
            imageRes = R.drawable.craterelluscornucopioides,
            bioRes = R.string.svart_trompetsopp_bio.toString(),
            properties = listOf(
                MushroomProperty("Ikke giftig", R.drawable.skull, isNegative = false),
                MushroomProperty("God matsopp", R.drawable.pan, isNegative = false)
            ),
            profile = allSpeciesProfiles.find { it.name == "Svart trompetsopp" }!!
        )

    )

    override fun getAllMushrooms(): List<Mushroom> {
        amount = mushroomList.size
        return mushroomList
    }

    override fun getAmountOfMushrooms(): Int {
        return amount
    }
}