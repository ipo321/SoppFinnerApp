package no.uio.ifi.in2000.team16.data.kartverket

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    private val baseUrl = "https://ws.geonorge.no/stedsnavn/v1"

    // Searches while user is writing
    suspend fun suggest(query: String): List<Place> {
        //if (query.length < 3) return emptyList()
        return try {
            val response: PlaceSearchResponse = httpClient.get("$baseUrl/navn") {
                url {
                    parameters.append("sok", query)
                    parameters.append("fuzzy", "true") // Allows the search text to not be 100% accurate
                    parameters.append("utkoordsys", "4326")
                    parameters.append("treffPerSide", "8") // Only 8 suggestions at a time
                }
            }.body()
            response.navn
        } catch (e: Exception) {
            Log.e("PlaceDataSource", "Error in suggest: ${e.message}")
            emptyList()
        }
    }
}