package no.uio.ifi.in2000.team16.data.met

import no.uio.ifi.in2000.team16.domain.model.WeatherHistory
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.*


/*
   Repository for the FROST Api than handles the data output and puts it into a Historical WeatherData module from the evaluation module
   Summarizes the data from the FROST Api
*/

class FrostRepository @Inject constructor(
    private val dataSource: FrostDataSource
) {
    // In-memory cache, should be made into a room database in the future for better persistence

    private val cache = mutableMapOf<Pair<Double, Double>, CacheEntry>()
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

    suspend fun weatherHistory(lat: Double, lon: Double): WeatherHistory? {

        //Checking cache
        val currentTime = System.currentTimeMillis()

        cache.entries.find { (coords, entry) ->
            (currentTime - entry.timestamp) < cacheMaxAgeMs &&
                    calculateDistance(lat, lon, coords.first, coords.second) < 1000
        }?.let { return it.value.data }

        //Fixing time range
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fourteenDaysAgo = 14*24*60*60*1000L
        val timeRange = "${sdf.format(currentTime-fourteenDaysAgo)}T00:00:00Z/${sdf.format(currentTime)}T00:00:00Z"

        // Fetching data from API
        val response = dataSource.fetchWeather(lat, lon, timeRange) ?: return null
        val rseries = response.data?.tseries ?: return null

        val allTempObs = mutableListOf<Double>()
        val allRainObs = mutableListOf<Double>()

        rseries.forEach { series ->
            val elementId = series.header?.extra?.element?.id?.lowercase() ?: ""

            // VIKTIG: Vi henter fra 'value' slik JSON-en din viser
            val values = series.observations.mapNotNull {
                it.body?.value?.toDoubleOrNull()
            }

            if (elementId.contains("air_temperature")) {
                allTempObs.addAll(values)
            } else if (elementId.contains("precipitation_amount")) {
                allRainObs.addAll(values)
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

        if (avgTemp != null || totalRain != null){
            cache [lat to lon] = CacheEntry(
                data = history,
                timestamp = currentTime
            )
        }

        return history
    }

}
