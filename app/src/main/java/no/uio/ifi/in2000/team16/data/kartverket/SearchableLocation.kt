package no.uio.ifi.in2000.team16.data.kartverket

import kotlinx.serialization.Serializable

/*
    This interface makes it possible to search for addresses and places with the same functions
 */
interface SearchableLocation {
    val title: String
    val subtitle: String
    val latitude: Double?
    val longitude: Double?
}

@Serializable
data class MetaData(
    val viserFra: Int? = null,
    val asciiKompatibel: Boolean? = null,
    val totaltAntallTreff: Int? = null,
    val viserTil: Int? = null,
    val sokeStreng: String? = null,
    val side: Int? = null,
    val treffPerSide: Int? = null
)

@Serializable
data class RepresentationPoint(
    val epsg: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
)