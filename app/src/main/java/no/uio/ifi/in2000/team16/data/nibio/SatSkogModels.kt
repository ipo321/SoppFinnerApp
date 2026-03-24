package no.uio.ifi.in2000.team16.data.nibio


data class SatSkogResponse(
    val treeSpecies: String? = null,
    val sprucePct: Int? = null,
    val pinePct: Int? = null,
    val broadleafPct: Int? = null,
    val fertilityCategory: String? = null,
    val siteIndex: Int? = null,
    val standAge: Int? = null,
    val minElevation: Int? = null,
    val maxElevation: Int? = null
)