package no.uio.ifi.in2000.team16.data.MushroomData

//import jakarta.inject.Inject
import javax.inject.Inject
import kotlin.Int
import kotlin.math.cos

class MushroomRepository @Inject constructor(
    private val mushroomDataSource: MushroomDataSource
) {

    suspend fun searchMushrooms(query: String): List<MushroomInfo> =
        mushroomDataSource.searchMushrooms(query).map { it.toMushroom() }

    //finn en
    suspend fun getMushroomDetails(id: String): MushroomInfo =
        mushroomDataSource.getMushroomById(id).toMushroom()


    //returns a OccurrenceResult objekt witch occurenses and a list of the occurenses.
    suspend fun getMushroomsNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double = 10.0,
        mushRoomType: String = "all mushrooms",
        limit: Int = 5 //hvor mange resultater man vil ha. defult er 5 resutlater.
    ): OccurrenceResult {
        val latOffset = radiusKm / 111.0
        val lonOffset = radiusKm / (111.0 * cos(Math.toRadians(lat)))//recalculation for the variation of latidiudes

        //making a square box around the location
        val latMin = lat - latOffset
        val latMax = lat + latOffset
        val lonMin = lon - lonOffset
        val lonMax = lon + lonOffset


        return mushroomDataSource.getOccurrencesNearLocation(mushRoomType, latMin, latMax, lonMin, lonMax, limit)
    }

    //returns an int with how many mushrooms are in the area
    suspend fun getMushroomCountNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double = 10.0,
        mushRoomType: String = "all mushrooms",
    ): Int {
        val result = getMushroomsNearLocation(lat, lon, radiusKm, mushRoomType,  0)
        return result.count
    }


    // Convert Species to MushroomInfo
    private fun MushroomSearchResult.toMushroom(): MushroomInfo =
        MushroomInfo(
            id = key.toString(),
            scientificName = scientificName,
            commonName = vernacularNames
                .firstOrNull { it.language == "nob" }?.vernacularName
                ?: vernacularNames.firstOrNull { it.language == "nno" }?.vernacularName
                ?: vernacularNames.firstOrNull()?.vernacularName,
            kingdom = kingdom,
            phylum = phylum,
            klass = `class`,
            order = order,
            family = family,
            genus = genus,
            genusKey = genusKey,
            species = species
        )
}