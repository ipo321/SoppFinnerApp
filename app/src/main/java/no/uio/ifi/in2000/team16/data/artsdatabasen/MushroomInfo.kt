package no.uio.ifi.in2000.team16.data.artsdatabasen
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class OccurrenceResult(
    val count: Int = 0,
    val results: List<Occurrence> = emptyList()
)

//Data about the report in location (findings)
@Serializable
data class Occurrence(
    val key: Long,
    val localSpeciesId: Int? = null,
    val decimalLatitude: Double? = null,
    val decimalLongitude: Double? = null,
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null
)

@Serializable
data class GeoJsonFeatureCollection(
    val type: String,
    val features: List<GeoJsonFeature>
)

@Serializable
data class GeoJsonFeature(
    val type: String,
    val geometry: GeoJsonGeometry,
    val properties: GeoJsonProperties
)

@Serializable
data class GeoJsonGeometry(
    val type: String,
    val coordinates: List<Double> // [lon, lat]
)

@Serializable
data class GeoJsonProperties(
    @SerialName("species_id")
    val speciesId: Int,
    val date: String
)
