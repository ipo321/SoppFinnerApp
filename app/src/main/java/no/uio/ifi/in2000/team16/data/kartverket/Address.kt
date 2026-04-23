package no.uio.ifi.in2000.team16.data.kartverket

import kotlinx.serialization.Serializable

@Serializable
data class AddressSearchResponse(
    val metadata: MetaData? = null,
    val adresser: List<Address> = emptyList()
)

@Serializable
data class MetaData(
    val viserFra: Int? = null,
    val asciiKompatibel: Boolean? = null,
    val totaltAntallTreff: Int? = null,
    val viserTil: Int? = null,
    val sokeStreng: String? = null,
    val side: Int? = null,
    val treffPerSide: Int? = null
)

@Serializable
data class Address(
    val adressenavn: String? = null,
    val adressetekst: String? = null,
    val adressetilleggsnavn: String? = null,
    val adressekode: Int? = null,
    val nummer: Int? = null,
    val bokstav: String? = null,
    val kommunenummer: String? = null,
    val kommunenavn: String? = null,
    val gardsnummer: Int? = null,
    val bruksnummer: Int? = null,
    val festenummer: Int? = null,
    val undernummer: Int? = null,
    val bruksenhetsnummer: List<String> = emptyList(),
    val objtype: String? = null,
    val poststed: String? = null,
    val postnummer: String? = null,
    val adressetekstutenadressetilleggsnavn: String? = null,
    val stedfestingverifisert: Boolean? = null,
    val representasjonspunkt: RepresentationPoint? = null,
    val oppdateringsdato: String? = null   // evt. Instant/LocalDateTime med custom serializer
) : SearchableLocation {
    override val title: String get() = adressetekst ?: ""
    override val subtitle: String get() = "$poststed, $kommunenavn".trim(',', ' ')
    override val latitude: Double? get() = representasjonspunkt?.lat
    override val longitude: Double? get() = representasjonspunkt?.lon
}

@Serializable
data class RepresentationPoint(
    val epsg: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
)
