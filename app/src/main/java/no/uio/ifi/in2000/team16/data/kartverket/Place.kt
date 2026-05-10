package no.uio.ifi.in2000.team16.data.kartverket

import kotlinx.serialization.Serializable

@Serializable
data class PlaceSearchResponse(
    val metadata: MetaData? = null,
    val navn: List<Place> = emptyList()
)

@Suppress("NonAsciiCharacters", "PropertyName")
@Serializable
data class Place(
    val stedsnavnnummer: Long? = null,
    val stedsnummer: Long? = null,
    val stedstatus: String? = null,
    val navnestatus: String? = null,
    val skrivemåte: String? = null,
    val skrivemåtestatus: String? = null,
    val språk: String? = null,
    val navneobjekttype: String? = null,
    val kommunenummer: String? = null,
    val kommunenavn: String? = null,
    val fylkesnummer: String? = null,
    val fylkesnavn: String? = null,
    val representasjonspunkt: RepresentationPoint? = null,
    val oppdateringsdato: String? = null
) : SearchableLocation {
    override val title: String get() = skrivemåte ?: ""
    
    override val subtitle: String get() {
        val type = navneobjekttype?.replaceFirstChar { it.uppercase() } ?: "Sted"
        val kommune = kommunenavn.takeIf { it != "null" && it != skrivemåte && !it.isNullOrBlank() } ?: ""
        val fylke = fylkesnavn.takeIf { it != "null" && it != skrivemåte && it != kommune && !it.isNullOrBlank() } ?: ""

        val locationParts = listOf(kommune, fylke).filter { it.isNotEmpty() }
        return when {
            locationParts.isNotEmpty() -> "$type i ${locationParts.joinToString(", ")}"
            (type != "Annen administrativ inndeling") -> type // We don't want this text, it doesn't tell the user much
            else -> ""
        }
    }
    
    override val latitude: Double? get() = representasjonspunkt?.nord ?: representasjonspunkt?.lat
    override val longitude: Double? get() = representasjonspunkt?.øst ?: representasjonspunkt?.lon
}
