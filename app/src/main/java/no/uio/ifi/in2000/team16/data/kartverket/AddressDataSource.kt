package no.uio.ifi.in2000.team16.data.kartverket

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    private val baseUrl = "https://ws.geonorge.no/adresser/v1"

    // Searches while user is writing
    suspend fun suggest(query: String): List<Address> {
        if (query.length < 3) return emptyList()
        return try {
            val response: AddressSearchResponse = httpClient.get("$baseUrl/sok") {
                url {
                    parameters.append("sok", query)
                    parameters.append("fuzzy", "true")
                    parameters.append("sokemodus", "OR")
                    parameters.append("utkoordsys", "4326")
                    parameters.append("treffPerSide", "5") // Only 5 suggestions at a time
                }
            }.body()
            response.adresser
        } catch (e: Exception) {
            Log.e("AddressDataSource", "Error in suggest: ${e.message}")
            emptyList()
        }
    }
}