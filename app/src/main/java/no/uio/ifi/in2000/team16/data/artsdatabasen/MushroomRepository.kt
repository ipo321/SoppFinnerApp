package no.uio.ifi.in2000.team16.data.artsdatabasen

//import jakarta.inject.Inject
import android.util.Log
import javax.inject.Inject
import kotlin.Int
import kotlin.math.cos
/*
repository that can search for mushrooms and get information about them
and the amount of mushrooms spotting in a certain area.
use getSpeciesNearLocation with latitude, longitude and species profile.
*/


class MushroomRepository @Inject constructor(
    private val mushroomDataSource: MushroomDataSource
) {

    suspend fun searchMushrooms(query: String): List<MushroomInfo> {
        Log.d("T16_Mushroom_Repository", "Searching for mushrooms with query: $query")
        return mushroomDataSource.searchMushrooms(query).map { it.toMushroom() }
    }


    suspend fun getMushroomDetails(id: String): MushroomInfo? {
        Log.d("T16_Mushroom_Repository", "Fetching details for mushroom ID: $id")
        val result = mushroomDataSource.getMushroomById(id)
        if (result == null){
            Log.w("T16_Mushroom_Repository","Could not find mushroom details for ID: $id")
            return null
        }
        return result.toMushroom()
    }

    suspend fun getSpeciesCountNearLocation(
        lat: Double,
        lon: Double,
        taxonKey: String,
        radiusKm: Double = 5.0,
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
        radiusKm: Double = 3.0,
        mushRoomType: String = "all mushrooms",
        limit: Int = 10
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

    suspend fun getMushroomInBoundingBox(
        north: Double,
        south: Double,
        east: Double,
        west: Double,
        mushRoomType: String,
        limit: Int = 10
    ): OccurrenceResult {
        return mushroomDataSource.getOccurrencesNearLocation(
            mushRoomType = mushRoomType,
            latMin = south,
            latMax = north,
            lonMin = west,
            lonMax = east,
            limit = limit
        )
    }

}