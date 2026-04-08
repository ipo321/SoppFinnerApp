package no.uio.ifi.in2000.team16.data.artsdatabasen


import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request

import javax.inject.Inject
import javax.inject.Singleton

//Gets data about mushrooms from GBIF API

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
        val mushroomkey = "34" //filters what kind of mushrooms we get
        // 5 — Kingdom Fungi (everything, including mold, yeast etc.)
        //34 — Phylum Basidiomycota (typical mushrooms etc.)
        //35 — Phylum Ascomycota (morels, truffles etc.)

        return try {
            val response: HttpResponse = httpClient.get(
                "$baseUrl/v1/occurrence/search"
            ) {
                url {
                    if (mushRoomType == "all mushrooms")//if the taxonKey parameter is empty we search hardcoded scope
                        parameters.append("phylumKey", mushroomkey)
                    else parameters.append("taxonKey", mushRoomType)
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

