package no.uio.ifi.in2000.team16.data.MushroomData

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.team16.model.Modul
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos

//Gets data about mushrooms from GBIF API

@Singleton
class MushroomDataSource @Inject constructor(
    private val httpClient : HttpClient
) {
    private val baseUrl = "https://api.gbif.org"

    suspend fun searchMushrooms(query: String): List<Species> {
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
            throw Exception("Failed to fetch mushrooms: ${response.status}")
        }

        val searchResult: SpeciesSearchResult = response.body()
        return searchResult.results
    }
    suspend fun getMushroomById(id: String): Species {
        val response: HttpResponse = httpClient.get(
            "$baseUrl/v1/species/$id"
        )

        if (response.status.value != 200) {
            throw Exception("Failed to fetch mushroom with id=$id: ${response.status}")
        }

        val species: Species = response.body()
        return species
    }

    suspend fun getMushroomCountNearLocation(
        taxonKey: String,
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double,
    ): Int {
        val mushroomkey = "34" //filters what kind of mushrooms we get
        // 5 — Kingdom Fungi (everything, including mold, yeast etc.)
        //34 — Phylum Basidiomycota (typical mushrooms like kantarell, sjampinjong, fluesopp etc.)
        //35 — Phylum Ascomycota (morels, truffles etc.)

        val response: HttpResponse = httpClient.get(
            "$baseUrl/v1/occurrence/search"
        ) {
            url {
                if(taxonKey == "all mushrooms")//if the taxonKey parameter is empty we search hardcoded scope
                    parameters.append("kingdomKey", mushroomkey)
                else parameters.append("taxonKey", taxonKey)
                parameters.append("decimalLatitude", "$latMin,$latMax")
                parameters.append("decimalLongitude", "$lonMin,$lonMax")
                parameters.append("hasCoordinate", "true")
                parameters.append("limit", "0")   // we don't need the actual results
            }
        }

        if (response.status.value != 200) {
            throw Exception("Failed to fetch count: ${response.status}")
        }

        val result: OccurrenceCountResult = response.body()
        return result.count
    }
}



@Module
@InstallIn(SingletonComponent::class)
object MushroomApiModule {

    @Provides
    @Singleton
    fun provideMushroomDataSource(httpClient: HttpClient): MushroomDataSource {
        return MushroomDataSource(httpClient)
    }
}