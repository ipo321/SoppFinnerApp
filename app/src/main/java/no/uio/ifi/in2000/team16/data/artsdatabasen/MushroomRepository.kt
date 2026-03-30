package no.uio.ifi.in2000.team16.data.artsdatabasen

//import jakarta.inject.Inject
import javax.inject.Inject
import kotlin.Int
import kotlin.math.cos
/*
repository that can search for mushrooms and get information about them
and the amount of mushrooms spottings in a certain area.
use getSpeciesNearLocation with latidude, longitude and the enumc class MushroomSpicies.


*/


class MushroomRepository @Inject constructor(
    private val mushroomDataSource: MushroomDataSource
) {

    suspend fun searchMushrooms(query: String): List<MushroomInfo> =
        mushroomDataSource.searchMushrooms(query).map { it.toMushroom() }


    suspend fun getMushroomDetails(id: String): MushroomInfo =
        mushroomDataSource.getMushroomById(id).toMushroom()

    suspend fun getSpeciesCountNearLocation(
        lat: Double,
        lon: Double,
        taxonKey: String,
        radiusKm: Double = 1.0,
    ): Int {
        val result = getMushroomsNearLocation(lat, lon, radiusKm, taxonKey)
        return result.count
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


    //returns a OccurrenceResult object witch a list of the occurenses inside a radius.
    suspend fun getMushroomsNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double = 1.0,
        mushRoomType: String = "all mushrooms",
        limit: Int = 100
    ): OccurrenceResult {
        val latOffset = radiusKm / 111.0
        val lonOffset = radiusKm / (111.0 * cos(Math.toRadians(lat)))

        val latMin = lat - latOffset
        val latMax = lat + latOffset
        val lonMin = lon - lonOffset
        val lonMax = lon + lonOffset

        return mushroomDataSource.getOccurrencesNearLocation(mushRoomType, latMin, latMax, lonMin, lonMax, limit)
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