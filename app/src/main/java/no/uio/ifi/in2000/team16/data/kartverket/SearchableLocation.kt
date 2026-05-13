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

@Suppress("NonAsciiCharacters", "PropertyName")
@Serializable
data class RepresentationPoint(
    val epsg: String? = null,
    val lat: Double? = null, // Address-API
    val lon: Double? = null, // Address-API
    val nord: Double? = null, // Place-API
    val øst: Double? = null  // Place-API
)