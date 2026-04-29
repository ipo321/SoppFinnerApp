package no.uio.ifi.in2000.team16.data.artsdatabasen



import android.util.Log
import javax.inject.Inject
import kotlin.math.cos
/*
repository that can search for mushrooms and get information about them
and the amount of mushrooms spotting in a certain area.
use getSpeciesNearLocation with latitude, longitude and species profile.
amount of mushroom results have a default limit that can be adjusted (limit)
and a default radius that can be adjusted (radiusKm).

*/


class MushroomRepository @Inject constructor(
    private val mushroomDataSource: MushroomDataSource,
    private val localMushroomDataSource: LocalMushroomDataSource
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


    //returns a OccurrenceResult object witch a list of the occurrences and a count inside a radius.
    suspend fun getMushroomsNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double = 100.0,
        mushRoomType: String = "no Mushroom",
        //limit: Int = 50
    ): OccurrenceResult {
        val latOffset = radiusKm / 111.0
        val lonOffset = radiusKm / (111.0 * cos(Math.toRadians(lat)))

        val latMin = lat - latOffset
        val latMax = lat + latOffset
        val lonMin = lon - lonOffset
        val lonMax = lon + lonOffset
        //makes mushroomType into an int that can be used in the local data source
        val localSpeciesId = LocalSpeciesIdMapper.fromName(mushRoomType)

        return localMushroomDataSource.getOccurrencesInBoundingBox(
            latMin = latMin,
            latMax = latMax,
            lonMin = lonMin,
            lonMax = lonMax,
            localSpeciesId = localSpeciesId
        )
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
    ): OccurrenceResult {
        val localSpeciesId = LocalSpeciesIdMapper.fromName(mushRoomType)

        return localMushroomDataSource.getOccurrencesInBoundingBox(
            latMin = south,
            latMax = north,
            lonMin = west,
            lonMax = east,
            localSpeciesId = localSpeciesId
        )
    }




}