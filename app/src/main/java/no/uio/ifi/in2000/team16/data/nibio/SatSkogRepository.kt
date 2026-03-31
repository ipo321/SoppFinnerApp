package no.uio.ifi.in2000.team16.data.nibio

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton
import no.uio.ifi.in2000.team16.domain.model.ForestData
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap


/*
   Repository for the SAT-SKOG Api than handles the data output and puts it into a ForestData module from the evaluation module
*/

@Singleton
class SatSkogRepository @Inject constructor(
    private val dataSource: SatSkogDataSource
){

    private val heatmapCache = ConcurrentHashMap<String, Deferred<ForestData?>>()
    private val preciseCache = ConcurrentHashMap<String, Deferred<ForestData?>>()

    private suspend fun fetchAndMapForestData(lat: Double, lon: Double): ForestData? {
        val response = dataSource.fetchForest(lat, lon) ?: return null

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
    suspend fun getForestData(lat: Double, lon: Double): ForestData? = coroutineScope {

        val cacheKey = String.format(Locale.US, "%.5f_%.5f", lat, lon)

        val deferredResult = preciseCache.getOrPut(cacheKey) {
            async {
                fetchAndMapForestData(lat, lon)
            }
        }
        deferredResult.await()
    }

    //Method for lower resolution (heatmap)
    suspend fun getForestDataForHeatmap(lat: Double, lon: Double): ForestData? = coroutineScope {
        val cacheKey = String.format(Locale.US, "%.3f_%.3f", lat, lon)

        val deferredResult = heatmapCache.getOrPut(cacheKey) {
            async {
                getForestData(lat, lon)
            }
        }
        deferredResult.await()
    }
}

