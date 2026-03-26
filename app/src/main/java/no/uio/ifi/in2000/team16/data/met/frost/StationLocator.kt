package no.uio.ifi.in2000.team16.data.met.frost

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import no.uio.ifi.in2000.team16.R
import javax.inject.Inject
import javax.inject.Singleton
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.*


/*
    Calling Frost with nearest casued the respone time to go from <1 to 8-10 seconds which was not optimal for the app. Without having to find the station the
    call went fast so we downloaded all the stations into a csv. Here we read it and returns the one that is closes based on a probably much simpler calculations
*/


@Singleton
class StationLocator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val allStations: List<LocalStation> by lazy {
        loadStationsFromCsv()
    }

    private fun loadStationsFromCsv(): List<LocalStation> {
        val stations = mutableListOf<LocalStation>()
        try {
            val inputStream = context.resources.openRawResource(R.raw.weatherstations)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLine()


            reader.forEachLine { line ->
                val tokens = line.split(",")
                if (tokens.size >= 6) {
                    stations.add(
                        LocalStation(
                            id = tokens[0].trim(),
                            name = tokens[1].trim(),
                            lat = tokens[2].trim().toDoubleOrNull() ?: 0.0,
                            lon = tokens[3].trim().toDoubleOrNull() ?: 0.0,
                            hasTemp = tokens[4].trim().toBoolean(),
                            hasRain = tokens[5].trim().toBoolean()
                        )
                    )
                }
            }
            reader.close()
        } catch (e: Exception) {
            println("Feil ved lesing av weatherstations.csv: ${e.message}")
        }
        return stations
    }

    fun findNearestStation(lat: Double, lon: Double, maxDistMeters: Double = 50000.0): List<String> {
        if (allStations.isEmpty()) return emptyList()
        val idsToFetch = mutableSetOf<String>()

        val nearestTemp = allStations
            .filter { it.hasTemp }
            .minByOrNull { calculateDistance(lat, lon, it.lat, it.lon) }

        if (nearestTemp != null && calculateDistance(lat, lon, nearestTemp.lat, nearestTemp.lon) <= maxDistMeters) {
            idsToFetch.add(nearestTemp.id)
        }

        val nearestRain = allStations
            .filter { it.hasRain }
            .minByOrNull { calculateDistance(lat, lon, it.lat, it.lon) }

        if (nearestRain != null && calculateDistance(lat, lon, nearestRain.lat, nearestRain.lon) <= maxDistMeters) {
            idsToFetch.add(nearestRain.id)
        }

        return idsToFetch.toList()
    }


    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
