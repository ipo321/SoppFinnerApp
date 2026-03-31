package no.uio.ifi.in2000.team16.data.met.locationForecast

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.team16.domain.model.WeatherData
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
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
    private val cacheMaxAgeMs = 8*60*60*1000L //8 hours

    private data class CacheEntry(
        val data: WeatherData,
        val timestamp: Long
    )
    //

    private fun roundToTwoDecimals(value: Double): Double {
        return (value * 100.0).roundToInt() / 100.0
    }


    suspend fun weatherNext8Hours(lat: Double, lon: Double): WeatherData? = coroutineScope{

        //Rounding to two decimals since that is the precision of the weather model (about 1x1 km)
        val roundedLat = roundToTwoDecimals(lat)
        val roundedLon = roundToTwoDecimals(lon)
        val cacheKey = "${roundedLat}_${roundedLon}"

        //Checking cache
        val existingDeferred = cache[cacheKey]

        if (existingDeferred != null) {
            val cached = existingDeferred.await()
            if (cached != null && (System.currentTimeMillis() - cached.timestamp) < cacheMaxAgeMs) {
                return@coroutineScope cached.data
            } else {
                cache.remove(cacheKey)
            }
        }

        //Fetching data from API
        val deferredResult = cache.getOrPut(cacheKey) {
            async {
                val response = dataSource.fetchWeather(roundedLat, roundedLon)
                val timeseries = response?.properties?.timeseries ?: return@async null
                val next8Hours = timeseries.take(8)
                if (next8Hours.isEmpty()) return@async null

                val totalRain = next8Hours.sumOf {
                    it.data.next_1_hours?.details?.precipitation_amount ?: 0.0
                }

                val maxWind = next8Hours.mapNotNull {
                    it.data.instant.details.wind_speed
                }.maxOrNull()

                val avgTemp = next8Hours.mapNotNull { it.data.instant.details.air_temperature }.average()
                    .takeIf { !it.isNaN() }
                val avgHumidity = next8Hours.mapNotNull { it.data.instant.details.relative_humidity }.average()
                    .takeIf { !it.isNaN() }
                val avgDewPoint = next8Hours.mapNotNull { it.data.instant.details.dew_point_temperature }.average()
                    .takeIf { !it.isNaN() }
                val avgFog = next8Hours.mapNotNull { it.data.instant.details.fog_area_fraction }.average()
                    .takeIf { !it.isNaN() }

                val generalForecast = next8Hours.firstOrNull()?.data?.next_12_hours
                val symbol = generalForecast?.summary?.symbol_code
                val rainProb = generalForecast?.details?.probability_of_precipitation

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



                CacheEntry(
                    data = weatherData,
                    timestamp = System.currentTimeMillis())
            }
        }

        deferredResult.await()?.data
    }
}


