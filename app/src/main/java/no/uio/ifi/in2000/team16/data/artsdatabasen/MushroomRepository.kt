package no.uio.ifi.in2000.team16.data.artsdatabasen

//import jakarta.inject.Inject
import no.uio.ifi.in2000.team16.domain.model.MushroomInfo
import no.uio.ifi.in2000.team16.domain.model.MushroomSearchResult
import no.uio.ifi.in2000.team16.domain.model.OccurrenceResult
import javax.inject.Inject
import kotlin.Int
import kotlin.math.cos
/*
repository that can search for mushrooms and get information about them
and the amount of mushrooms spottings in a certain area.
use getSpeciesNearLocation with latidude, longitude and the enumc class MushroomSpicies.


*/

enum class MushroomSpecies(val taxonKey: String) {
    CHANTERELLE("5249504"),
    FUNNEL_CHANTERELLE("2554536"),
    PORCINI("5954958")
}
class MushroomRepository @Inject constructor(
    private val mushroomDataSource: MushroomDataSource
) {

    suspend fun searchMushrooms(query: String): List<MushroomInfo> =
        mushroomDataSource.searchMushrooms(query).map { it.toMushroom() }

    //get one spesiffic by id.
    suspend fun getMushroomDetails(id: String): MushroomInfo =
        mushroomDataSource.getMushroomById(id).toMushroom()

    suspend fun getSpeciesCountNearLocation(
        lat: Double,
        lon: Double,
        species: MushroomSpecies,
        radiusKm: Double = 10.0,
    ): Int = getMushroomCountNearLocation(
        lat = lat,
        lon = lon,
        radiusKm = radiusKm,
        mushRoomType = species.taxonKey
    )

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


    suspend fun getChanterelleNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double = 10.0
    ): Int = getMushroomCountNearLocation(
        lat = lat,
        lon = lon,
        radiusKm = radiusKm,
        mushRoomType = "5249504"
    )

    suspend fun getFunnelChanterelleNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double = 10.0
    ): Int = getMushroomCountNearLocation(
        lat = lat,
        lon = lon,
        radiusKm = radiusKm,
        mushRoomType = "2554536"
    )

    suspend fun getPorciniNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double = 10.0
    ): Int = getMushroomCountNearLocation(
        lat = lat,
        lon = lon,
        radiusKm = radiusKm,
        mushRoomType = "5954958"
    )

    //returns a OccurrenceResult object witch a list of the occurenses inside a radius.
    suspend fun getMushroomsNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double = 10.0,
        mushRoomType: String = "all mushrooms",
        limit: Int = 5 //amount of results. default 5.
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