package no.uio.ifi.in2000.team16.data.kartverket

import kotlinx.serialization.Serializable

@Serializable
data class PlaceSearchResponse(
    val metadata: MetaData? = null,
    val navn: List<Place> = emptyList()
)

@Serializable
data class Place(
    val stedsnummer: Long? = null,
    val navn: String? = null,
    val navnestatus: String? = null,
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
    override val title: String get() = navn ?: ""
    
    override val subtitle: String get() {
        val type = navneobjekttype?.replaceFirstChar { it.uppercase() } ?: "Sted"
        val kommune = kommunenavn.takeIf { it != "null" && it != navn && !it.isNullOrBlank() } ?: ""
        val fylke = fylkesnavn.takeIf { it != "null" && it != navn && it != kommune && !it.isNullOrBlank() } ?: ""

        val locationParts = listOf(kommune, fylke).filter { it.isNotEmpty() }
        return if (locationParts.isNotEmpty()) {
            "$type i ${locationParts.joinToString(", ")}"
        } else {
            type
        }

//        return when {
//            kommune.isNotEmpty() && fylke.isNotEmpty() && kommune != fylke -> "$type i $kommune i $fylke"
//            kommune.isNotEmpty() -> "$type i $kommune"
//            fylke.isNotEmpty() -> "$type i $fylke"
//            else -> type
//        }
    }
    
    override val latitude: Double? get() = representasjonspunkt?.nord ?: representasjonspunkt?.lat
    override val longitude: Double? get() = representasjonspunkt?.øst ?: representasjonspunkt?.lon
}
