package no.uio.ifi.in2000.team16.data.nibio

import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton
import no.uio.ifi.in2000.team16.domain.model.ForestData
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.cancellation.CancellationException


/*
   Repository for the SAT-SKOG Api than handles the data output and puts it into
   a ForestData module from the evaluation module. Utilizes concurrent caching to
   prevent redundant network calls for nearby locations
*/

interface SatSkogRepositoryInterface {
    suspend fun fetchAndMapForestData(
        lat: Double,
        lon: Double
    ): ForestData?

    suspend fun getForestData(
        lat: Double,
        lon: Double
    ): ForestData?

    suspend fun getForestDataForHeatmap(
        lat: Double,
        lon: Double
    ): ForestData?
}
@Singleton
class SatSkogRepository @Inject constructor(
    private val dataSource: SatSkogDataSource
) : SatSkogRepositoryInterface {
    // Caches to avoid hammering the API. Heatmap uses lower precision to group requests.
    private val heatmapCache = ConcurrentHashMap<String, Deferred<ForestData?>>()
    private val preciseCache = ConcurrentHashMap<String, Deferred<ForestData?>>()

    override suspend fun fetchAndMapForestData(lat: Double, lon: Double): ForestData? {
        val response = dataSource.fetchForest(lat, lon) ?: run {
            Log.w("T16_NIBIO_Repo", "No forest data found for $lat, $lon")
            return null
        }

        return ForestData(
            dominantTreeSpecies = response.treeSpecies,
            sprucePercentage = response.sprucePct,
            pinePercentage = response.pinePct,
            broadleafPercentage = response.broadleafPct,
            fertilityCategory = response.fertilityCategory,
            siteIndex = response.siteIndex,
            standAge = response.standAge,
            minElevation = response.minElevation,
            maxElevation = response.maxElevation
        )
    }
    override suspend fun getForestData(lat: Double, lon: Double): ForestData? = coroutineScope {
        // Formats to 5 decimal places (approx. 11 meter precision)
        val cacheKey = String.format(Locale.US, "%.4f_%.4f", lat, lon)

        if (preciseCache.containsKey(cacheKey)) {
            Log.d("T16_NIBIO_Repo", "Returning cached data for $cacheKey")
        } else {
            Log.d("T16_NIBIO_Repo", "No cache found, fetching new data for $cacheKey")
        }

        val deferredResult = preciseCache.getOrPut(cacheKey) {
            async {
                fetchAndMapForestData(lat, lon)
            }
        }
        return@coroutineScope try {
            deferredResult.await()
        } catch (e: Exception) {
            preciseCache.remove(cacheKey, deferredResult)
            if (e is CancellationException) throw e
            null
        }
    }

    //Method for lower resolution (heatmap) to prevent unnecessary network calls
    override suspend fun getForestDataForHeatmap(lat: Double, lon: Double): ForestData? = coroutineScope {
        // Formats to 3 decimal places (approx. 111 meter precision)
        val cacheKey = String.format(Locale.US, "%.3f_%.3f", lat, lon)

        if (heatmapCache.containsKey(cacheKey)) {
            Log.d("T16_NIBIO_Repo", "Returning cached heatmap data for $cacheKey")
        } else {
            Log.d("T16_NIBIO_Repo", "No cache found, fetching new heatmap data for $cacheKey")
        }

        val deferredResult = heatmapCache.getOrPut(cacheKey) {
            async {
                getForestData(lat, lon)
            }
        }
        return@coroutineScope try {
            deferredResult.await()
        } catch (e: Exception) {
            heatmapCache.remove(cacheKey, deferredResult)
            if (e is CancellationException) throw e
            null
        }
    }
}

