package no.uio.ifi.in2000.team16.data.artsdatabasen
import kotlinx.serialization.Serializable


data class MushroomInfo(
    val id: String,
    val scientificName: String,
    val commonName: String?,
    val kingdom: String?,
    val phylum: String?,
    val klass: String?,
    val order: String?,
    val family: String?,
    val genus: String?,
    val genusKey: Int?,
    val species: String?
)

@Serializable
data class OccurrenceResult(
    val count: Int = 0,
    val results: List<Occurrence> = emptyList()
)

//Data about the report in location (findings)
@Serializable
data class Occurrence(
    val key: Long,
    val scientificName: String? = null,
    val species: String? = null,
    val speciesKey: Int? = null,
    val decimalLatitude: Double? = null,
    val decimalLongitude: Double? = null,
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null
)

@Serializable
data class VernacularName(
    val vernacularName: String,
    val language: String? = null
)


@Serializable
data class SpeciesSearchResult(
    val results: List<MushroomSearchResult>,
    //val count: Int = 0,
)


@Serializable
data class MushroomSearchResult(
    val key: Long,
    val scientificName: String,
    val vernacularNames: List<VernacularName> = emptyList(),
    val kingdom: String? = null,
    val phylum: String? = null,
    val `class`: String? = null,
    val order: String? = null,
    val family: String? = null,
    val genus: String? = null,
    val genusKey: Int? = null,
    val species: String? = null
)
