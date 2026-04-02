package no.uio.ifi.in2000.team16.data.met.frost

import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.team16.domain.model.WeatherHistory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


/*
   Repository for the FROST Api than handles the data output and puts it into a Historical WeatherData module from the evaluation module
   Summarizes the data from the FROST Api
*/

class FrostRepository @Inject constructor(
    private val dataSource: FrostDataSource,
    private val stationLocator: StationLocator
) {
    // In-memory cache, should be made into a room database in the future for better persistence

    private val cache = ConcurrentHashMap<String, Deferred<CacheEntry?>>()
    private val cacheMaxAgeMs = 8*60*60*1000L //8 hours
    private val cacheMutex = Mutex()
    private data class CacheEntry(
        val data: WeatherHistory,
        val timestamp: Long
    )

    suspend fun weatherHistory(lat: Double, lon: Double): WeatherHistory? = coroutineScope{
        val currentTime = System.currentTimeMillis()

        //Find best station
        val stationIdsList = stationLocator.findNearestStation(lat, lon, 50000.0)

        //Cache key
        val cacheKey = if (stationIdsList.isNotEmpty()) {
            "STATIONS_" + stationIdsList.joinToString(",")
        } else {
            "FALLBACK_${(lat * 100).toInt()}_${(lon * 100).toInt()}"
        }

        val deferredResult = cacheMutex.withLock {
            val existingDeferred = cache[cacheKey]

            if (existingDeferred != null) {
                existingDeferred
            } else {
                Log.d("T16_Frost_Repository", "No cache found, starting API call for $cacheKey")
                val newJob = async {

                    //Fixing time range
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val fourteenDaysAgo = 14 * 24 * 60 * 60 * 1000L
                    val timeRange = "${sdf.format(currentTime - fourteenDaysAgo)}T00:00:00Z/${
                        sdf.format(currentTime)
                    }T00:00:00Z"


                    // Fetching data from API
                    var rseries: List<TimeSeries>? = null

                    //QUICK PATH
                    if (stationIdsList.isNotEmpty()) {
                        val idsString = stationIdsList.joinToString(",") // F.eks "18500,18950"
                        val fastResponse = dataSource.fetchWeather(idsString, timeRange)
                        rseries = fastResponse?.data?.tseries
                    }

                    val hasValidData = rseries?.any { it.observations.isNotEmpty() } == true

                    // FALLBACK
                    if (!hasValidData) {
                        Log.w(
                            "T16_Frost_Repository",
                            "No valid data from preset stations. Triggering fallback for lat: $lat, lon: $lon"
                        )
                        val slowResponse = dataSource.fetchWeatherFallback(lat, lon, timeRange)
                        rseries = slowResponse?.data?.tseries ?: return@async null
                    }


                    val allTempObs = mutableListOf<Double>()
                    val allRainObs = mutableListOf<Double>()

                    var foundTemp = false
                    var foundRain = false

                    rseries.forEach { series ->
                        val elementId = series.header?.extra?.element?.id?.lowercase() ?: ""
                        val values = series.observations.mapNotNull {
                            (it.body?.value ?: it.body?.data)?.toDoubleOrNull()
                        }

                        if (elementId.contains("air_temperature") && !foundTemp && values.isNotEmpty()) {
                            allTempObs.addAll(values)
                            foundTemp = true
                        } else if (elementId.contains("precipitation_amount") && !foundRain && values.isNotEmpty()) {
                            allRainObs.addAll(values)
                            foundRain = true
                        }
                    }
                    // calculation
                    val avgTemp = if (allTempObs.isNotEmpty()) allTempObs.average() else null
                    val minTemp = if (allTempObs.isNotEmpty()) allTempObs.minOrNull() else null
                    val maxTemp = if (allTempObs.isNotEmpty()) allTempObs.maxOrNull() else null
                    val totalRain = if (allRainObs.isNotEmpty()) allRainObs.sum() else null

                    val history = WeatherHistory(
                        avgTempPast14Days = avgTemp,
                        totalRainPast14Days = totalRain,
                        minTempPast14Days = minTemp,
                        maxTempPast14Days = maxTemp
                    )

                    if (avgTemp != null || totalRain != null) {
                        Log.i(
                            "T16_Frost_Repository",
                            "Successfully processed and cached historical weather for $cacheKey"
                        )
                        CacheEntry(
                            data = history,
                            timestamp = currentTime
                        )
                    } else {
                        Log.w(
                            "T16_Frost_Repository",
                            "Failed to calculate valid temp/rain for $cacheKey"
                        )
                        null
                    }
                }
                cache[cacheKey] = newJob
                newJob
            }
        }

        val cached = deferredResult.await()

        if (cached != null && (currentTime - cached.timestamp) < cacheMaxAgeMs) {
            cache.remove(cacheKey)
            return@coroutineScope null
           } else if (cached != null) {
                Log.d("T16_Frost_Repository", "Returning historical weather for $cacheKey")
           }

        return@coroutineScope deferredResult.await()?.data
    }

}
