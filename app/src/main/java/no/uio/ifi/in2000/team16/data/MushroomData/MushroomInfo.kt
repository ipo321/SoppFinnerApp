package no.uio.ifi.in2000.team16.data.MushroomData
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
    val species: String?
)

@Serializable
data class OccurrenceCountResult(
    val count: Int = 0
)

@Serializable
data class VernacularName(
    val vernacularName: String,
    val language: String? = null
)


@Serializable
data class SpeciesSearchResult(
    val results: List<Species>
)

@Serializable
data class Species(
    val key: Int,
    val scientificName: String,
    val vernacularNames: List<VernacularName> = emptyList(),
    val kingdom: String? = null,
    val phylum: String? = null,
    val `class`: String? = null,
    val order: String? = null,
    val family: String? = null,
    val genus: String? = null,
    val species: String? = null
)
