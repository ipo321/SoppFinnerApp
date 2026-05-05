package no.uio.ifi.in2000.team16.data.artsdatabasen



import javax.inject.Inject
import kotlin.math.cos
/*

*/

interface MushroomRepositoryInterface {
    val defaultKmRadius: Double
    suspend fun getMushroomsNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double = defaultKmRadius,
        mushroomType: String,
    ): OccurrenceResult

    suspend fun getMushroomInBoundingBox(
        north: Double,
        south: Double,
        east: Double,
        west: Double,
        mushroomType: String,
    ): OccurrenceResult
}

class MushroomRepository @Inject constructor(
    private val localMushroomDataSource: LocalMushroomDataSourceInterface
) : MushroomRepositoryInterface {

    override val defaultKmRadius = 10000000.0//quick fix give alot of range
    private val kmPerDegreeLat = 111.0 //for the offset of latitude.

    // In data source: signature becomes localSpeciesId: Int (non-null)
    //returns a OccurrenceResult object witch a list of the occurrences and a count inside a radius.
    override suspend fun getMushroomsNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double,
        mushroomType: String,
    ): OccurrenceResult {
        val latOffset = radiusKm / kmPerDegreeLat
        val lonOffset = radiusKm / (kmPerDegreeLat * cos(Math.toRadians(lat)))

        val latMin = lat - latOffset
        val latMax = lat + latOffset
        val lonMin = lon - lonOffset
        val lonMax = lon + lonOffset
        //makes mushroomType into an int that can be used in the local data source
        //returns early if it is null
        val localSpeciesId = LocalSpeciesIdMapper.fromName(mushroomType)
            ?: return OccurrenceResult()

        return localMushroomDataSource.getOccurrencesInBoundingBox(
            latMin = latMin,
            latMax = latMax,
            lonMin = lonMin,
            lonMax = lonMax,
            localSpeciesId = localSpeciesId
        )
    }

    override suspend fun getMushroomInBoundingBox(
        north: Double,
        south: Double,
        east: Double,
        west: Double,
        mushroomType: String,
    ): OccurrenceResult {
        //makes mushroomType into an int that can be used in the local data source
        //returns early if it is null
        val localSpeciesId = LocalSpeciesIdMapper.fromName(mushroomType)
            ?: return OccurrenceResult()

        return localMushroomDataSource.getOccurrencesInBoundingBox(
            latMin = south,
            latMax = north,
            lonMin = west,
            lonMax = east,
            localSpeciesId = localSpeciesId
        )
    }
}