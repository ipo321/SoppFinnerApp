package no.uio.ifi.in2000.team16.data.nibio

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject
import javax.inject.Singleton

/*
    API for getting Forest data from NIBIO and their AR5 WMS services. Uses a similar method to
    SAT-Skog using Get Feature Info and GML.
*/

interface ForestDataSourceInterface {
    suspend fun fetchArealType(
        lat: Double,
        lon: Double
    ): String?
}

@Singleton
class ForestDataSource @Inject constructor(
    private val httpClient: HttpClient
) : ForestDataSourceInterface {
    private val baseUrl = "https://wms.nibio.no/cgi-bin/ar5"

    override suspend fun fetchArealType(lat: Double, lon: Double): String? {
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
            Log.d("T16_AR5_CALL", "Fetching AR5 data from URL: $url")
            val responseText = httpClient.get(url).bodyAsText()

            val regex = "<[^>]*artype_beskrivelse>(.*?)</[^>]*artype_beskrivelse>".toRegex(RegexOption.IGNORE_CASE)
            val match = regex.find(responseText)

            val result = match?.groupValues?.get(1)?.trim()

            if (result != null) {
                Log.d("T16_AR5_DataSource", "Successfully fetched AR5 for lat: $lat, lon: $lon")
            } else {
                Log.w("T16_AR5_DataSource", "No AR5 data found for lat: $lat, lon: $lon")
            }

            result
        } catch (e: Exception) {
            Log.e("T16_AR5_DataSource", "Network error when fetching AR5: ${e.message}", e)
            null
        }
    }
}