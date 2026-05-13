package no.uio.ifi.in2000.team16.data.met.locationForecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
    Dataclasses for handling the information coming from the MET API.
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
    @SerialName("next_1_hours")
    val next1Hours: MetNext1Hours? = null,
    @SerialName("next_12_hours")
    val next12Hours: MetNext12Hours? = null
)


@Serializable
data class MetNext12Hours(
    val summary: MetSummary? = null,
    val details: MetProbabilityDetails? = null
)

@Serializable
data class MetSummary(
    @SerialName("symbol_code")
    val symbolCode: String? = null
)

@Serializable
data class MetProbabilityDetails(
    @SerialName("probability_of_precipitation")
    val probabilityOfPrecipitation: Double? = null
)

@Serializable
data class MetInstant(
    val details: MetDetails
)

@Serializable
data class MetDetails(
    @SerialName("air_temperature")
    val airTemperature: Double? = null,
    @SerialName("relative_humidity")
    val relativeHumidity: Double? = null,
    @SerialName("wind_speed")
    val windSpeed: Double? = null,
    @SerialName("dew_point_temperature")
    val dewPointTemperature: Double? = null,
    @SerialName("fog_area_fraction")
    val fogAreaFraction: Double? = null
)

@Serializable
data class MetNext1Hours(
    val details: MetPrecipDetails
)

@Serializable
data class MetPrecipDetails(
    @SerialName("precipitation_amount")
    val precipitationAmount: Double? = null
)


