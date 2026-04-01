package no.uio.ifi.in2000.team16.data.nibio

import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/*
    Repository for the AR5 API, than handles the data output and puts it into a ForestData module from the evaluation module.
    Uses the same method as SAT-SKOG
 */
@Singleton
class ForestRepository @Inject constructor(
    private val forestDataSource: ForestDataSource
) {
    // Cache
    private val cache = ConcurrentHashMap<String, Deferred<String?>>()
    suspend fun getArealType(lat: Double, lon: Double): String? = coroutineScope {
        val cacheKey = String.format(Locale.US, "%.4f_%.4f", lat, lon)

        if (cache.containsKey(cacheKey)) {
            Log.d("T16_AR5_Repo", "Returning cached data for $cacheKey")
        } else {
            Log.d("T16_AR5_Repo", "No cache found, fetching new data for $cacheKey")
        }

        val deferredResult = cache.getOrPut(cacheKey) {
            async {
                forestDataSource.fetchArealType(lat, lon)
            }
        }
        deferredResult.await()
    }
}