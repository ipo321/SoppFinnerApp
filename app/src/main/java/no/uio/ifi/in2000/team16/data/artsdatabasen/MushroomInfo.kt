package no.uio.ifi.in2000.team16.data.artsdatabasen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * The result of an occurrence query — a list of sightings and how many were found.
 * [count] always equals [results].size; it is kept as a convenience field.
 */
@Serializable
data class OccurrenceResult(
    val count: Int = 0,
    val results: List<Occurrence> = emptyList()
)

/** A single recorded mushroom sighting from the local GeoJSON dataset. */
@Serializable
data class Occurrence(
    val key: Long,
    val localSpeciesId: Int? = null,  // maps to LocalSpeciesIdMapper ID
    val decimalLatitude: Double? = null,
    val decimalLongitude: Double? = null,
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null
)

// ── Internal GeoJSON parsing models ─────────────────────────────────────────
// These mirror the structure of res/raw/mushroom_map_data.geojson exactly.
// They are only used by LocalMushroomDataSource and should not leak to other layers.

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
