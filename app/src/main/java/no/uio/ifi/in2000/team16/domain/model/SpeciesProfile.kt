package no.uio.ifi.in2000.team16.domain.model


/*
Dataclass for what information about the mushroom itself that goes into the algorithm
*/

data class SpeciesProfile(
    val name: String,
    val taxKey: String,

    //Forest data
    val optimalBonitet: Double,
    val optimalForestAge: Double,
    val minForestAge: Double,

    val spruceAffinity: Float,
    val pineAffinity: Float,
    val broadleafAffinity: Float,

    //Weather history
    val optimalAvgTemp: Double,
    val hardinessMinTemp: Double,
    val maxToleranceTemp: Double,
    val optimalTotalRain14d: Double,

    //Hardcoded
    val activeMonths: List<Int>,
    val optimalCurrentHumidity: Double,
    val maxCurrentRainMm: Double,

    //ML specific
    val minRaw: Float,
    val maxRaw: Float,
)
