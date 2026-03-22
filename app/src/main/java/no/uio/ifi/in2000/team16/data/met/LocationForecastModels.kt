package no.uio.ifi.in2000.team16.data.met

import kotlinx.serialization.Serializable

/*
    Dataclasses for handling the information coming from the MET API. Might be move the file to a different location
*/

@Serializable
data class MetResponse(
    val properties: MetProperties
)

@Serializable
data class MetProperties(
    val timeseries: List<MetTimeSeries>
)

@Serializable
data class MetTimeSeries(
    val time: String,
    val data: MetData
)

@Serializable
data class MetData(
    val instant: MetInstant,
    val next_1_hours: MetNext1Hours? = null,
    val next_12_hours: MetNext12Hours? = null
)


@Serializable
data class MetNext12Hours(
    val summary: MetSummary? = null,
    val details: MetProbabilityDetails? = null
)

@Serializable
data class MetSummary(
    val symbol_code: String? = null
)

@Serializable
data class MetProbabilityDetails(
    val probability_of_precipitation: Double? = null
)

@Serializable
data class MetInstant(
    val details: MetDetails
)

@Serializable
data class MetDetails(
    val air_temperature: Double? = null,
    val relative_humidity: Double? = null,
    val wind_speed: Double? = null,
    val dew_point_temperature: Double? = null,
    val fog_area_fraction: Double? = null,
)

@Serializable
data class MetNext1Hours(
    val details: MetPrecipDetails
)

@Serializable
data class MetNext1HoursDetails(
    val details: MetPrecipDetails? = null
)

@Serializable
data class MetPrecipDetails(
    val precipitation_amount: Double? = null
)


