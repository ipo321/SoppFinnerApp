package no.uio.ifi.in2000.team16.data.artsdatabasen


import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request

import javax.inject.Inject
import javax.inject.Singleton

/*Gets data about mushrooms from GBIF API
mainly used for getting occurrences near a location.

can get list of mushrooms in the Artstdatabank.
getOccurrencesNearLocation is hardcoded with 34 as mushroomkey,->
-> most calls to the function uses mushroomType witch is a specific code for a mushroom spices


 */


@Singleton
class MushroomDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    private val baseUrl = "https://api.gbif.org"

    suspend fun searchMushrooms(query: String): List<MushroomSearchResult> {
        return try{
            val response: HttpResponse = httpClient.get(
                "$baseUrl/v1/species/search"
            ) {
                url {
                    parameters.append("q", query) //search word
                    parameters.append("language", "nob")   // favor Norwegian vernacular names
                    parameters.append("rank", "SPECIES") //the level of the search
                    parameters.append("highertaxonKey", "5")  // only fungi
                }
            }

            if (response.status.value != 200) {
                Log.w("T16_Mushroom_DataSource", "API returned status ${response.status.value} for search: $query")
                return emptyList()
            }

            val searchResult: SpeciesSearchResult = response.body()
            Log.d("T16_Mushroom_DataSource", "Search for '$query' returned ${searchResult.results.size} results.")
            return searchResult.results
        } catch (e: Exception) {
            Log.e("T16_Mushroom_DataSource", "Network error during searchMushrooms: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getMushroomById(id: String): MushroomSearchResult? {
        return try {
            val response: HttpResponse = httpClient.get(
                "$baseUrl/v1/species/$id"
            )

            if (response.status.value != 200) {
                Log.w("T16_Mushroom_DataSource", "API returned status ${response.status.value} for ID: $id")
                return null
            }

            response.body()
        } catch (e: Exception) {
            Log.e("T16_Mushroom_DataSource", "Network error during getMushroomById: ${e.message}", e)
            null
        }

    }

    suspend fun getOccurrencesNearLocation(
        mushRoomType: String, //maybe find a better name.
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double,
        limit: Int,
    ): OccurrenceResult {

        return try {
            val response: HttpResponse = httpClient.get(
                "$baseUrl/v1/occurrence/search"
            ) {
                url {
                    parameters.append("taxonKey", mushRoomType)
                    parameters.append("decimalLatitude", "$latMin,$latMax")
                    parameters.append("decimalLongitude", "$lonMin,$lonMax")
                    parameters.append("hasCoordinate", "true")
                    parameters.append("limit", limit.toString())   //Amount of results.
                }
            }
            Log.d("T16_Mushroom_DataSource", "GBIF request URL: ${response.request.url}")

            //println("GBIF request URL: ${response.request.url}")
            if (response.status.value != 200) {
                Log.w("T16_Mushroom_DataSource", "API returned status ${response.status.value} for Type: $mushRoomType")
                return OccurrenceResult()
            }

            val result: OccurrenceResult = response.body()
            Log.d("T16_Mushroom_DataSource", "Found ${result.count} occurrences in bounding box for type: $mushRoomType")
            result

        } catch (e: Exception){
            Log.e("T16_Mushroom_DataSource", "Network error during getOccurrencesNearLocation: ${e.message}", e)
            OccurrenceResult() // Returns an empty object if there's an error
        }
    }
}

