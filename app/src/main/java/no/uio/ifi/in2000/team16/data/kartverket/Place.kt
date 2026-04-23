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
        val kommune = kommunenavn ?: ""
        val fylke = fylkesnavn ?: ""
        val parts = listOf(type, kommune, fylke).filter { it.isNotBlank() && it != "null" }

        return when {
            kommune.isNotEmpty() && fylke.isNotEmpty() && kommune != fylke -> "$type $kommune $fylke"
            kommune.isNotEmpty() -> "$type $kommune"
            fylke.isNotEmpty() -> "$type $fylke"
            else -> type
        }
    }
    
    override val latitude: Double? get() = representasjonspunkt?.lat
    override val longitude: Double? get() = representasjonspunkt?.lon
}
