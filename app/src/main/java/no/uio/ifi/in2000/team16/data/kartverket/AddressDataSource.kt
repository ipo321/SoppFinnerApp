package no.uio.ifi.in2000.team16.data.kartverket

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

interface AddressDataSourceInterface {

    suspend fun suggest(
        query: String
    ): List<Address>
}

@Singleton
class AddressDataSource @Inject constructor(
    private val httpClient: HttpClient
) : AddressDataSourceInterface {
    private val baseUrl = "https://ws.geonorge.no/adresser/v1"

    // Searches while user is writing
    override suspend fun suggest(query: String): List<Address> {
        return try {
            val response: AddressSearchResponse = httpClient.get("$baseUrl/sok") {
                url {
                    parameters.append("sok", query)
                    parameters.append("fuzzy", "true") // Allows the search text to not be 100% accurate
                    parameters.append("sokemodus", "AND") // Everything in search must be in result. Can switch to OR if we don't want this.
                    parameters.append("utkoordsys", "4326")
                    parameters.append("treffPerSide", "8") // Only 8 suggestions at a time
                }
            }.body()
            response.adresser
        } catch (e: Exception) {
            Log.e("AddressDataSource", "Error in suggest: ${e.message}")
            emptyList()
        }
    }
}