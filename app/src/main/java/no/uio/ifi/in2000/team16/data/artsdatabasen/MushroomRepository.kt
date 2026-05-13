package no.uio.ifi.in2000.team16.data.artsdatabasen

import javax.inject.Inject
import kotlin.math.cos

/*
 * Repository that provides mushroom occurrence data to the rest of the app.
 *
 * Acts as the single entry point for querying [LocalMushroomDataSource].
 * Converts human-readable mushroom names and geographic inputs into the
 * format the data source expects (species ID + bounding box coordinates).
 */

interface MushroomRepositoryInterface {
    val defaultKmRadius: Double

    /**
     * Returns occurrences of [mushroomType] within [radiusKm] kilometres of the given point.
     *
     * Converts the radius into a lat/lon bounding box, then delegates to the data source.
     */
    suspend fun getMushroomsNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double = defaultKmRadius,
        mushroomType: String,
    ): OccurrenceResult

    /**
     * Returns occurrences of [mushroomType] within an explicit bounding box.
     *
     * Use this when you already have the visible map bounds (e.g. for heatmap calculation).
     */
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

    // Effectively "no radius limit" (10000000.0) — returns finds across all of Norway and beyond.
    // this is a shortcut we made since we did not have enough time to adjust it.
    // can be reworked to use the size of the mapView so it only renders inn the points
    // that are visible on screen.
    // This is a known quick fix; a proper default would be something like 100.0 km.
    override val defaultKmRadius = 10000000.0

    // 1 degree of latitude ≈ 111 km anywhere on Earth (latitude lines are equally spaced)
    private val kmPerDegreeLat = 111.0

    override suspend fun getMushroomsNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double,
        mushroomType: String,
    ): OccurrenceResult {
        // Latitude offset: 1 degree = 111 km
        val latOffset = radiusKm / kmPerDegreeLat

        // Longitude degrees shrink towards the poles, so we correct with cos(lat).
        // At 60°N (Norway), 1 degree of longitude ≈ 55 km instead of 111 km.
        val lonOffset = radiusKm / (kmPerDegreeLat * cos(Math.toRadians(lat)))

        val latMin = lat - latOffset
        val latMax = lat + latOffset
        val lonMin = lon - lonOffset
        val lonMax = lon + lonOffset

        // Convert the mushroom name to the internal species ID used by the data source.
        // Return an empty result immediately if the name is not recognized.
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
        // Convert the mushroom name to the internal species ID used by the data source.
        // Return an empty result immediately if the name is not recognized.
        val localSpeciesId = LocalSpeciesIdMapper.fromName(mushroomType)
            ?: return OccurrenceResult()

        // Map compass directions to the lat/lon min/max the data source expects:
        // north = latMax, south = latMin, east = lonMax, west = lonMin
        return localMushroomDataSource.getOccurrencesInBoundingBox(
            latMin = south,
            latMax = north,
            lonMin = west,
            lonMax = east,
            localSpeciesId = localSpeciesId
        )
    }
}