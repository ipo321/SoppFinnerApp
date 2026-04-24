package no.uio.ifi.in2000.team16.data.artsdatabasen

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
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
@Singleton
class LocalMushroomDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val json: Json
) {

    companion object {
        private const val TAG = "T16_LocalMushroomDataSource"
    }
    private val mutex = Mutex()

    private var cachedOccurrences: OccurrenceResult? = null

    suspend fun getAllOccurrences(): OccurrenceResult {
        return cachedOccurrences ?: mutex.withLock {
            // Check again inside the lock in case another coroutine loaded it first
            cachedOccurrences ?: loadFromDisk().also { cachedOccurrences = it }
        }
    }




    private suspend fun loadFromDisk(): OccurrenceResult  {
        return try {
            val text = withContext(Dispatchers.IO) {
                context.resources
                    .openRawResource(R.raw.mushroom_map_data)
                    .bufferedReader()
                    .use { it.readText() } //read text form file and automatically closes afterwords.
            }
            withContext(Dispatchers.Default){
                parseGeoJson(text) //sends raw jason string to parse function.
            }
        }catch (e: Exception) {
            Log.e(TAG, "Failed to load mushroom data: ${e.message}", e)
            OccurrenceResult()
        }

}

    private fun parseGeoJson(text: String): OccurrenceResult {
        val geo = json.decodeFromString<GeoJsonFeatureCollection>(text)
        val occurrence = geo.features.mapIndexed { index, feature ->
            featureToOccurrence(index, feature)
        }
        return OccurrenceResult(
            count = occurrence.size,
            results = occurrence
        )

    }
    //changes the geojson feature to an object of occurrence
    private fun featureToOccurrence(index: Int, feature: GeoJsonFeature): Occurrence {
        val dateParts = feature.properties.date.split("-")
        return Occurrence(
            key = index.toLong(),
            localSpeciesId = feature.properties.speciesId,
            decimalLatitude = feature.geometry.coordinates.getOrNull(1),
            decimalLongitude = feature.geometry.coordinates.getOrNull(0),
            year  = dateParts.getOrNull(0)?.toIntOrNull(),
            month = dateParts.getOrNull(1)?.toIntOrNull(),
            day   = dateParts.getOrNull(2)?.toIntOrNull()
        )
    }

    suspend fun getOccurrencesInBoundingBox(
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double,
        localSpeciesId: Int? // null = unrecognized species
    ): OccurrenceResult = withContext(Dispatchers.Default) {
        if (localSpeciesId == null) return@withContext OccurrenceResult()
        val all = getAllOccurrences()
        Log.d(TAG, "Filtering ${all.count} occurrences...")
        val bySpecies = all.results.filter { it.localSpeciesId == localSpeciesId }


        Log.d(TAG, "Species filter result: ${bySpecies.size} occurrences")

        //filters out all occurrences that are not in the bounding box.
        val filtered = bySpecies.filter { occurrence ->
            val lat = occurrence.decimalLatitude
            val lon = occurrence.decimalLongitude
            if (lat == null || lon == null) return@filter false
            lat in latMin..latMax && lon in lonMin..lonMax
        }


        Log.d(TAG, "Filter result: ${filtered.size} occurrences")

        OccurrenceResult(
            count = filtered.size,
            results = filtered
        )
    }
}