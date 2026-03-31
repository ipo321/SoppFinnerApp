package no.uio.ifi.in2000.team16.data.met.frost

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.team16.domain.model.WeatherHistory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.math.*


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

    private data class CacheEntry(
        val data: WeatherHistory,
        val timestamp: Long
    )
    //

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    suspend fun weatherHistory(lat: Double, lon: Double): WeatherHistory? = coroutineScope{

        val currentTime = System.currentTimeMillis()
        //FIND BEST STATION
        val stationIdsList = stationLocator.findNearestStation(lat, lon, 50000.0)

        //Cache key
        val cacheKey = if (stationIdsList.isNotEmpty()) {
            "STATIONS_" + stationIdsList.joinToString(",")
        } else {
            "FALLBACK_${(lat * 100).toInt()}_${(lon * 100).toInt()}"
        }

        //Checking cache
        val existingDeferred = cache[cacheKey]
        if (existingDeferred != null) {
            val cached = existingDeferred.await()
            if (cached != null && (currentTime - cached.timestamp) < cacheMaxAgeMs) {
                return@coroutineScope cached.data
            } else {
                cache.remove(cacheKey)
            }
        }


        val deferredResult = cache.getOrPut(cacheKey) {
            async{

                //Fixing time range
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val fourteenDaysAgo = 14*24*60*60*1000L
                val timeRange = "${sdf.format(currentTime-fourteenDaysAgo)}T00:00:00Z/${sdf.format(currentTime)}T00:00:00Z"


                // Fetching data from API
                var rseries: List<TimeSeries>? = null

                //QUICK PATH
                if (stationIdsList.isNotEmpty()) {
                    val idsString = stationIdsList.joinToString(",") // F.eks "18500,18950"
                    val fastResponse = dataSource.fetchWeather(idsString, timeRange)
                    rseries = fastResponse?.data?.tseries
                }

                val hasValidData = rseries?.any { it.observations?.isNotEmpty() == true } == true

                // FALLBACK
                if (!hasValidData) {
                    println("Frost: Fallback for $lat, $lon")
                    val slowResponse = dataSource.fetchWeatherFallback(lat, lon, timeRange)
                    rseries = slowResponse?.data?.tseries ?: return@async null
                }


                val allTempObs = mutableListOf<Double>()
                val allRainObs = mutableListOf<Double>()

                var foundTemp = false
                var foundRain = false

                rseries.forEach { series ->
                    val elementId = series.header?.extra?.element?.id?.lowercase() ?: ""
                    val values = series.observations?.mapNotNull {
                        (it.body?.value ?: it.body?.data)?.toDoubleOrNull()
                    } ?: emptyList()

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
                    CacheEntry(
                        data = history,
                        timestamp = currentTime
                    )
                } else {
                    null
                }
            }
        }
        return@coroutineScope deferredResult.await()?.data
    }

}
