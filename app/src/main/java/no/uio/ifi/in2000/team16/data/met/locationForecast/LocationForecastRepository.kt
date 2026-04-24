package no.uio.ifi.in2000.team16.data.met.locationForecast

import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.team16.domain.model.WeatherData
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt

/*
   Repository for the MET Api than handles the data output and puts it into a WeatherData module from the evaluation module
   Summarizes the data from the MET Api
*/

class LocationForecastRepository @Inject constructor(
    private val dataSource: LocationForecastDataSource
) {
    // In-memory cache, should be made into a room database in the future for better persistence

    private val cache = ConcurrentHashMap<String, Deferred<CacheEntry?>>()
    private val cacheMaxAgeMs = 8 * 60 * 60 * 1000L //8 hours

    private data class CacheEntry(
        val data: WeatherData,
        val timestamp: Long
    )
    //

    private fun roundToTwoDecimals(value: Double): Double {
        return (value * 100.0).roundToInt() / 100.0
    }


    suspend fun weatherNext8Hours(lat: Double, lon: Double): WeatherData? = coroutineScope {

        //Rounding to two decimals since that is the precision of the weather model (about 1x1 km)
        val roundedLat = roundToTwoDecimals(lat)
        val roundedLon = roundToTwoDecimals(lon)
        val cacheKey = "${roundedLat}_${roundedLon}"

        //Checking cache
        val existingDeferred = cache[cacheKey]

        if (existingDeferred != null) {
            val cached = existingDeferred.await()
            if (cached != null && (System.currentTimeMillis() - cached.timestamp) < cacheMaxAgeMs) {
                Log.d("T16_Weather_Repo", "Returning cached data for $cacheKey")
                return@coroutineScope cached.data
            } else {
                Log.d("T16_Weather_Repo", "Cache expired for $cacheKey")
                cache.remove(cacheKey, existingDeferred)
            }
        }

        Log.d("T16_Weather_Repo", "No cache found, fetching new data for $cacheKey")


        //Fetching data from API
        val deferredResult = cache.getOrPut(cacheKey) {
            async {
                val response = dataSource.fetchWeather(roundedLat, roundedLon)

                val timeseries = response?.properties?.timeseries ?: run {
                    Log.w("T16_Weather_Repo", "No timeseries found in response for $cacheKey")
                    return@async null
                }
                val next8Hours = timeseries.take(8)
                if (next8Hours.isEmpty()) {
                    Log.w("T16_Weather_Repo", "No timeseries response for $cacheKey")
                    return@async null
                }

                val totalRain = next8Hours.sumOf {
                    it.data.next1Hours?.details?.precipitationAmount ?: 0.0
                }

                val maxWind = next8Hours.mapNotNull {
                    it.data.instant.details.windSpeed
                }.maxOrNull()

                val avgTemp =
                    next8Hours.mapNotNull { it.data.instant.details.airTemperature }.average()
                        .takeIf { !it.isNaN() }
                val avgHumidity =
                    next8Hours.mapNotNull { it.data.instant.details.relativeHumidity }.average()
                        .takeIf { !it.isNaN() }
                val avgDewPoint =
                    next8Hours.mapNotNull { it.data.instant.details.dewPointTemperature }
                        .average()
                        .takeIf { !it.isNaN() }
                val avgFog =
                    next8Hours.mapNotNull { it.data.instant.details.fogAreaFraction }.average()
                        .takeIf { !it.isNaN() }

                val generalForecast = next8Hours.firstOrNull()?.data?.next12Hours
                val symbol = generalForecast?.summary?.symbolCode
                val rainProb = generalForecast?.details?.probabilityOfPrecipitation

                val weatherData = WeatherData(
                    temp = avgTemp,
                    rain = totalRain,
                    humidity = avgHumidity,
                    windSpeed = maxWind,
                    dewPoint = avgDewPoint,
                    fogFraction = avgFog,
                    symbolCode = symbol,
                    probOfRain = rainProb
                )

                Log.i("T16_Weather_Repo", "Successfully fetched weather data for $cacheKey")

                CacheEntry(
                    data = weatherData,
                    timestamp = System.currentTimeMillis()
                )
            }
        }

        return@coroutineScope try {
            deferredResult.await()?.data
        } catch (e: Exception) {
            cache.remove(cacheKey, deferredResult)
            if (e is CancellationException) throw e
            null
        }
    }
}


