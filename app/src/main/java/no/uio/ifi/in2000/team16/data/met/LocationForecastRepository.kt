package no.uio.ifi.in2000.team16.data.met

import no.uio.ifi.in2000.team16.domain.model.WeatherData
import javax.inject.Inject

/*
   Repository for the MET Api than handles the data output and puts it into a WeatherData module from the evaluation module
   Summarizes the data from the MET Api t
*/

class LocationForecastRepository @Inject constructor(
    private val dataSource: LocationForecastDataSource
) {
    suspend fun WeatherNext8Hours(lat: Double, lon: Double): WeatherData? {
        val response = dataSource.fetchWeather(lat, lon) ?: return null
        val next8Hours = response.properties.timeseries.take(8)
        if (next8Hours.isEmpty()) return null

        /*
        val totalRain = next8Hours.sumOf {
            it.data.next_1_hours?.details?.precipitation_amount ?: 0.0
        }*/
        val maxWind = next8Hours.mapNotNull {
            it.data.instant.details.wind_speed
        }.maxOrNull()

        val avgTemp = next8Hours.mapNotNull { it.data.instant.details.air_temperature }.average()
            .takeIf { !it.isNaN() }
        val avgHumidity =
            next8Hours.mapNotNull { it.data.instant.details.relative_humidity }.average()
                .takeIf { !it.isNaN() }
        val avgDewPoint =
            next8Hours.mapNotNull { it.data.instant.details.dew_point_temperature }.average()
                .takeIf { !it.isNaN() }


        return WeatherData(
            temp = avgTemp,
            rain = null,
            humidity = avgHumidity,
            windSpeed = maxWind,
            dewPoint = avgDewPoint,
            fogFraction = null,
            symbolCode = null,
            probOfRain = null
        )

    }
}