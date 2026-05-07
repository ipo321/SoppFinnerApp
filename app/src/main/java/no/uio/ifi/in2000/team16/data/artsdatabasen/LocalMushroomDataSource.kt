package no.uio.ifi.in2000.team16.data.artsdatabasen

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team16.R
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source that loads mushroom occurrence data from a GeoJSON file.
 *
 * The GeoJSON file (res/raw/mushroom_map_data) contains mushroom sightings across Norway,
 * each with a geographic location and a species ID.
 *
 * Data is loaded once from disk and then cached in memory for the lifetime of the app.
 * Use [getOccurrencesInBoundingBox] to query sightings within a geographic area.
 */
interface LocalMushroomDataSourceInterface {

    /**
     * Returns all occurrences of a given species within the specified geographic bounding box.
     *
     * @param latMin Southern boundary (minimum latitude)
     * @param latMax Northern boundary (maximum latitude)
     * @param lonMin Western boundary (minimum longitude)
     * @param lonMax Eastern boundary (maximum longitude)
     * @param localSpeciesId The app-internal species ID to filter by (see [LocalSpeciesIdMapper])
     */
    suspend fun getOccurrencesInBoundingBox(
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double,
        localSpeciesId: Int
    ): OccurrenceResult
}

@Singleton
class LocalMushroomDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val json: Json
) : LocalMushroomDataSourceInterface {

    companion object {
        private const val TAG = "T16_LocalMushroomDataSource"
    }

    // Prevents multiple coroutines from loading the file at the same time on first access
    private val mutex = Mutex()

    // Full occurrence list, loaded once and kept for the app's lifetime
    private var cachedOccurrences: OccurrenceResult? = null

    // Pre-grouped by species ID so queries skip a full scan of the entire dataset.
    // Unknown species IDs are stored under key -1.
    private var speciesIndex: Map<Int, List<Occurrence>> = emptyMap()

    /**
     * Returns the full occurrence list, loading from disk on first call.
     * Uses double-checked locking: the null check outside the lock avoids acquiring
     * the lock on every call after the data is loaded, while the check inside the lock
     * prevents duplicate loads if two coroutines reach this point simultaneously.
     */
    private suspend fun getAllOccurrences(): OccurrenceResult {
        return cachedOccurrences ?: mutex.withLock {
            // Re-check inside the lock — another coroutine may have loaded it while we waited
            cachedOccurrences ?: loadFromDisk().also {
                cachedOccurrences = it
                // Build the species index once so future queries are O(1) per species lookup
                speciesIndex = it.results.groupBy { occ -> occ.localSpeciesId ?: -1 }
            }
        }
    }

    /**
     * Reads the raw GeoJSON file from app resources and parses it into [OccurrenceResult].
     *
     * File I/O runs on [Dispatchers.IO]; JSON parsing runs on [Dispatchers.Default]
     * because it is CPU-bound work, not I/O-bound.
     */
    private suspend fun loadFromDisk(): OccurrenceResult {
        return try {
            val text = withContext(Dispatchers.IO) {
                context.resources
                    .openRawResource(R.raw.mushroom_map_data)
                    .bufferedReader()
                    .use { it.readText() } // use{} closes the stream automatically
            }
            withContext(Dispatchers.Default) {
                parseGeoJson(text)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read mushroom_map_data from raw resources", e)
            OccurrenceResult()
        } catch (e: SerializationException) {
            Log.e(TAG, "Failed to parse mushroom GeoJSON — file is malformed", e)
            OccurrenceResult()
        }
    }

    /** Deserializes the GeoJSON string and converts each feature to an [Occurrence]. */
    private fun parseGeoJson(text: String): OccurrenceResult {
        val geo = json.decodeFromString<GeoJsonFeatureCollection>(text)
        val occurrences = geo.features.mapIndexed { index, feature ->
            featureToOccurrence(index, feature)
        }
        return OccurrenceResult(
            count = occurrences.size,
            results = occurrences
        )
    }

    /**
     * Maps a single GeoJSON feature to an [Occurrence].
     *
     * GeoJSON coordinates are ordered [longitude, latitude] (not lat/lon),
     * so index 0 is longitude and index 1 is latitude.
     */
    private fun featureToOccurrence(index: Int, feature: GeoJsonFeature): Occurrence {
        val dateParts = feature.properties.date.split("-") // format: YYYY-MM-DD
        return Occurrence(
            key = index.toLong(),
            localSpeciesId = feature.properties.speciesId,
            decimalLatitude  = feature.geometry.coordinates.getOrNull(1), // index 1 = lat
            decimalLongitude = feature.geometry.coordinates.getOrNull(0), // index 0 = lon
            year  = dateParts.getOrNull(0)?.toIntOrNull(),
            month = dateParts.getOrNull(1)?.toIntOrNull(),
            day   = dateParts.getOrNull(2)?.toIntOrNull()
        )
    }

    /**
     * Returns occurrences of [localSpeciesId] that fall within the given bounding box.
     *
     * Looks up the species from the pre-built index (O(1)) then filters by coordinates.
     * Runs on [Dispatchers.Default] because the bounding box filter is CPU-bound.
     */
    override suspend fun getOccurrencesInBoundingBox(
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double,
        localSpeciesId: Int
    ): OccurrenceResult = withContext(Dispatchers.Default) {
        getAllOccurrences() // ensures the index is built before we query it

        val bySpecies = speciesIndex[localSpeciesId] ?: emptyList()
        Log.d(TAG, "Species filter result: ${bySpecies.size} occurrences")

        val filtered = bySpecies.filter { occurrence ->
            val lat = occurrence.decimalLatitude
            val lon = occurrence.decimalLongitude
            if (lat == null || lon == null) return@filter false
            lat in latMin..latMax && lon in lonMin..lonMax
        }

        OccurrenceResult(
            count = filtered.size,
            results = filtered
        )
    }
}