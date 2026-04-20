package no.uio.ifi.in2000.team16.data.arcGIS

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val x: Double, // ArcGIS bruker x og y
    val y: Double
) {
    val lat: Double get() = y
    val lon: Double get() = x
}

@Serializable
data class Candidate(
    val address: String? = null,
    val location: Location? = null
)

@Serializable
data class Response (
    val candidates: List<Candidate>? = emptyList()
)

@Serializable
data class Suggestion(
    val text: String,
    val magicKey: String,
    val isCollection: Boolean
)

@Serializable
data class SuggestionResponse(
    val suggestions: List<Suggestion> = emptyList()
)
