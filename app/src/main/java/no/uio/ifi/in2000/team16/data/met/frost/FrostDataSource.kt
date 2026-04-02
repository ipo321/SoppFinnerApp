package no.uio.ifi.in2000.team16.data.met.frost

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton


/*
   API for Frost. SHOULD CHANGE FROM RANKED AND NEAREST in upcomming solution. this should take the loading time down from 8 seconds to 1
*/
@Singleton
class FrostDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    private val elements = "air_temperature,sum(precipitation_amount P1D)"
    private val authUser = "d3b7c216-1329-47bb-b09e-c22b6353cc0b"

    suspend fun fetchWeather(stationIds: String, timeRange: String): FrostResponse? {
        val url = "https://frost-rc.met.no/api/v1/obs/base/get"

        return try {
            val response = httpClient.get(url) {
                header("User-Agent", "IN2000 Team 16")
                basicAuth(authUser, "")
                parameter("stationids", stationIds)
                parameter("elementids", elements)
                parameter("time", timeRange)
                parameter("incobs", "true")
            }
            response.body<FrostResponse>()
        } catch (e: Exception) {
            Log.e("T16_Frost_DataSource","Network error when calling Frost Base API: ${e.message}",e)
            null
        }
    }
    suspend fun fetchWeatherFallback(lat: Double, lon: Double, timeRange: String): FrostResponse? {
        val url = "https://frost-rc.met.no/api/v1/obs/ranked/get"
        val nearestJson = """{"maxdist":10,"maxcount":5,"points":[{"lon":$lon,"lat":$lat}]}"""

        return try {
            val response = httpClient.get(url) {
                header("User-Agent", "IN2000 Team 16")
                basicAuth(authUser, "")
                parameter("nearest", nearestJson)
                parameter("elementids", elements)
                parameter("time", timeRange)
                parameter("incobs", "true")
            }
            println("Frost API URL: ${response.call.request.url}")
            response.body<FrostResponse>()
        } catch (e: Exception) {
            Log.e("T16_Frost_DataSource","Network error when calling Frost Fallback API: ${e.message}",e)
            null
            }
        }
    }
