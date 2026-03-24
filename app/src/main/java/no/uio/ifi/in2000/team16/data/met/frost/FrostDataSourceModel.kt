package no.uio.ifi.in2000.team16.data.met.frost

import kotlinx.serialization.Serializable

/*
Dataclasses for handling the information that will be put into the evaluation module.
Important to look at for all API and repositories as this is what your repository should return.
*/

@Serializable
data class FrostResponse(
    val data: FrostData? = null
)

@Serializable
data class FrostData(
    val tstype: String? = null,
    val tseries: List<TimeSeries> = emptyList()
)

@Serializable
data class TimeSeries(
    val header: Header? = null,
    val observations: List<Observation> = emptyList()
)

@Serializable
data class Header(
    val id: HeaderId? = null,
    val extra: Extra? = null
)

@Serializable
data class HeaderId(
    val stationid: Int? = null // I JSON: 18700 (Int)
)

@Serializable
data class Extra(
    val element: Element? = null,
    val station: Station? = null
)

@Serializable
data class Element(
    val id: String? = null
)

@Serializable
data class Station(
    val shortname: String? = null,
    val location: List<Location> = emptyList()
)

@Serializable
data class Location(
    val value: LocationValue? = null
)

@Serializable
data class LocationValue(
    val latitude: String? = null,
    val longitude: String? = null
)

@Serializable
data class Observation(
    val time: String? = null,
    val body: Body? = null
)

@Serializable
data class Body(
    val data: String? = null,
    val value: String? = null
)

//Class used by the datasource to find closest station
data class LocalStation(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val hasTemp: Boolean,
    val hasRain: Boolean
)