package no.uio.ifi.in2000.team16.data.nibio

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject
import javax.inject.Singleton

/*
    API for getting weather data from NIBIO and AR5 data
*/

@Singleton
class ForestDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    private val baseUrl = "https://wms.nibio.no/cgi-bin/ar5"

    suspend fun fetchArealType(lat: Double, lon: Double): String? {
        val offset = 0.0001
        val minLat = lat - offset
        val minLon = lon - offset
        val maxLat = lat + offset
        val maxLon = lon + offset

        val url = "$baseUrl?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetFeatureInfo" +
                "&BBOX=$minLat,$minLon,$maxLat,$maxLon" +
                "&CRS=EPSG:4326&WIDTH=10&HEIGHT=10&I=5&J=5" +
                "&LAYERS=Arealtype&QUERY_LAYERS=Arealtype" +
                "&INFO_FORMAT=application/vnd.ogc.gml"

        return try {
            Log.d("AR5_CALL", "Fetching AR5 data from URL: $url")
            val responseText = httpClient.get(url).bodyAsText()

            val regex = "<[^>]*artype_beskrivelse>(.*?)</[^>]*artype_beskrivelse>".toRegex(RegexOption.IGNORE_CASE)
            val match = regex.find(responseText)

            val result = match?.groupValues?.get(1)?.trim()

            if (result != null) {
                Log.d("AR5_RESULT", "Successfully extracted arealtype: $result")
            } else {
                Log.w("AR5_RESULT", "No <arealtype> tag found in the response. Coordinate might be outside mapped areas (e.g., in the ocean).")
            }

            result
        } catch (e: Exception) {
            Log.e("AR5_ERROR", "Network error while fetching AR5 data: ${e.message}", e)
            null
        }
    }
}