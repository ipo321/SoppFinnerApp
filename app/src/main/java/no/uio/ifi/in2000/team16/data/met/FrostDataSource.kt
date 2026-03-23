package no.uio.ifi.in2000.team16.data.met

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FrostDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun fetchWeather(lat: Double, lon: Double, timeRange: String): FrostResponse? {
        val url = "https://frost-rc.met.no/api/v1/obs/ranked/get"

        val elements = "air_temperature,sum(precipitation_amount P1D)"
        val nearestJson = """{"maxdist":10,"maxcount":2,"points":[{"lon":$lon,"lat":$lat}]}"""

        return try {
            val response = httpClient.get(url) {
                header("User-Agent", "IN2000 Team 16")
                basicAuth("d3b7c216-1329-47bb-b09e-c22b6353cc0b", "")
                parameter("nearest", nearestJson)
                parameter("elementids", elements)
                parameter("time", timeRange)
                parameter("incobs", "true")
            }
            println("Frost API URL: ${response.call.request.url}")
            response.body<FrostResponse>()
        } catch (e: Exception) {
            println("Network error Frost: ${e.message}")
            null
            }
        }
    }
