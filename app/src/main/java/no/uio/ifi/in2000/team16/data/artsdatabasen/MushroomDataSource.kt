package no.uio.ifi.in2000.team16.data.artsdatabasen


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.team16.domain.model.MushroomSearchResult
import no.uio.ifi.in2000.team16.domain.model.OccurrenceResult
import no.uio.ifi.in2000.team16.domain.model.SpeciesSearchResult

import javax.inject.Inject
import javax.inject.Singleton

//Gets data about mushrooms from GBIF API

@Singleton
class MushroomDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    private val baseUrl = "https://api.gbif.org"

    suspend fun searchMushrooms(query: String): List<MushroomSearchResult> {
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
            println("failed to response")
            throw Exception("Failed to fetch mushrooms: ${response.status}")
        }

        val searchResult: SpeciesSearchResult = response.body()
        return searchResult.results
    }

    suspend fun getMushroomById(id: String): MushroomSearchResult {
        val response: HttpResponse = httpClient.get(
            "$baseUrl/v1/species/$id"
        )

        if (response.status.value != 200) {
            println("get mushroom by id fail")
            throw Exception("Failed to fetch mushroom with id=$id: ${response.status}")
        }

        val mushroomSearchResult: MushroomSearchResult = response.body()
        return mushroomSearchResult
    }

    suspend fun getOccurrencesNearLocation(
        mushRoomType: String, //maby find a better name.
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double,
        limit: Int,
    ): OccurrenceResult {
        val mushroomkey = "34" //filters what kind of mushrooms we get
        // 5 — Kingdom Fungi (everything, including mold, yeast etc.)
        //34 — Phylum Basidiomycota (typical mushrooms like kantarell, sjampinjong, fluesopp etc.)
        //35 — Phylum Ascomycota (morels, truffles etc.)

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

        //println("GBIF request URL: ${response.request.url}")
        if (response.status.value != 200) {
            println("failed to response occurance location")
            throw Exception("Failed to fetch count: ${response.status}")
        }

        val result: OccurrenceResult = response.body()
        return result
    }

}

