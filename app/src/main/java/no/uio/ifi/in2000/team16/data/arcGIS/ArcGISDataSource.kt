package no.uio.ifi.in2000.team16.data.arcGIS

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team16.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArcGISDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    private val baseUrl = "https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer"

    // Autocompletes while user is writing
    suspend fun suggest(query: String): List<Suggestion> {
        if (query.length < 3) return emptyList()
        return try {
            val response: SuggestionResponse = httpClient.get("$baseUrl/suggest") {
                url {
                    parameters.append("f", "json")
                    parameters.append("text", query)
                    parameters.append("location", "10.75,59.91") // Priorities Oslo
                    parameters.append("countryCode", "NOR")      // Only Norway
                    parameters.append("maxSuggestions", "5")     // Suggests only 5
                    parameters.append("token", BuildConfig.ARCGIS_API_KEY)
                }
            }.body()
            response.suggestions
        } catch (e: Exception) {
            Log.e("ArcGISDataSource", "Error in suggest: ${e.message}")
            emptyList()
        }
    }

    // Gets coordinates when address is chosen or searched for
    suspend fun geocodeSearch(query: String): Candidate? {
        return try {
            val response: Response = httpClient.get("$baseUrl/findAddressCandidates") {
                url {
                    parameters.append("f", "json")
                    parameters.append("singleLine", query)
                    parameters.append("outFields", "*")
                    parameters.append("maxLocations", "1")
                    parameters.append("outSR", "4324") // Makes coordinates standard
                    parameters.append("token", BuildConfig.ARCGIS_API_KEY)
                }
            }.body()
            response.candidates?.firstOrNull()
        } catch (e: Exception) {
            Log.e("ArcGISDataSource", "Error in geocode: ${e.message}")
            null
        }
    }
}
