package no.uio.ifi.in2000.team16.data.MET

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import javax.inject.Inject
import javax.inject.Singleton

/*
    API for getting weather data from MET and their locationforecast
*/

@Singleton
class LocationForecastDataSource @Inject constructor(
    private val httpClient: HttpClient
){
    suspend fun fetchWeather(lat: Double, lon: Double): MetResponse? {
        val url = "https://in2000.api.met.no/weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lon"  //Complete gives fog area fraction and condensation point

        return try {
            httpClient.get(url){
                header("User-Agent", "IN2000 Team 16")
            }.body<MetResponse>()
            } catch (e: Exception) {
                println("Network error Met: ${e.message}")
                null
        }

    }
}

