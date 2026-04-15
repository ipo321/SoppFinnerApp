package no.uio.ifi.in2000.team16.data.artsdatabasen

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team16.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalMushroomDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {

    companion object {
        private const val TAG = "T_16_LocalMushroomDataSource"
    }

    @Volatile
    private var cachedOccurrences: OccurrenceResult? = null

    suspend fun getAllOccurrences(): OccurrenceResult {
        cachedOccurrences?.let {
            Log.d(TAG, "Returning ${it.count} occurrences from cache")
            return it }

        return withContext(Dispatchers.IO) {
            Log.d(TAG, "Loading mushroom_map_data from raw resources")
            val text = context.resources
                .openRawResource(R.raw.mushroom_map_data)
                .bufferedReader()
                .use { it.readText() }

            val geo = json.decodeFromString<GeoJsonFeatureCollection>(text)

            val occurrences = geo.features.mapIndexed { index, feature ->
                val lon = feature.geometry.coordinates.getOrNull(0)
                val lat = feature.geometry.coordinates.getOrNull(1)
                val dateParts = feature.properties.date.split("-")

                Occurrence(
                    key = index.toLong(),
                    scientificName = null,
                    species = null,
                    speciesKey = null,
                    localSpeciesId = feature.properties.speciesId,
                    decimalLatitude = lat,
                    decimalLongitude = lon,
                    year = dateParts.getOrNull(0)?.toIntOrNull(),
                    month = dateParts.getOrNull(1)?.toIntOrNull(),
                    day = dateParts.getOrNull(2)?.toIntOrNull()
                )
            }

            val result = OccurrenceResult(
                count = occurrences.size,
                results = occurrences
            )
            Log.d(TAG, "Built OccurrenceResult with ${result.count} occurrences")
            cachedOccurrences = result
            result
        }
    }

    suspend fun getOccurrencesInBoundingBox(
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double,
        localSpeciesId: Int? // null = all types
    ): OccurrenceResult {
        val all = getAllOccurrences()
        Log.d(
            TAG,
            "Filtering ${all.count} occurrences " +
                    "bbox=[lat:$latMin..$latMax, lon:$lonMin..$lonMax], " +
                    "localSpeciesId=${localSpeciesId ?: "ALL"}"
        )

        val bySpecies = if (localSpeciesId == null) {
            all.results
        } else {
            all.results.filter { it.localSpeciesId == localSpeciesId }
        }

        Log.d(TAG, "Species filter result: ${bySpecies.size} occurrences")

        val filtered = bySpecies.filter { occ ->
            val lat = occ.decimalLatitude
            val lon = occ.decimalLongitude
            if (lat == null || lon == null) return@filter false
            lat in latMin..latMax && lon in lonMin..lonMax
        }


        Log.d(TAG, "Filter result: ${filtered.size} occurrences")

        return OccurrenceResult(
            count = filtered.size,
            results = filtered
        )
    }
}
