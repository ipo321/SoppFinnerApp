package no.uio.ifi.in2000.team16.data.nibio

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject
import javax.inject.Singleton

/*
    API for getting Forest data from NIBIO and their SAT-SKOG
*/

@Singleton
class SatSkogDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun fetchForest(lat: Double, lon: Double): SatSkogResponse? {

        //Making a square box around the location
        val offset = 0.0001
        val latMin = lat - offset
        val lonMin = lon - offset
        val latMax = lat + offset
        val lonMax = lon + offset

        val url = "https://wms.nibio.no/cgi-bin/satskog" +
                "?SERVICE=WMS" +
                "&VERSION=1.3.0" +
                "&REQUEST=GetFeatureInfo" +
                "&BBOX=$latMin,$lonMin,$latMax,$lonMax" +
                "&CRS=EPSG:4326" +
                "&WIDTH=10&HEIGHT=10" +
                "&LAYERS=Alder" +
                "&QUERY_LAYERS=Alder" +
                "&I=5&J=5" +
                "&INFO_FORMAT=application/vnd.ogc.gml"

        return try {
            val responseText = httpClient.get(url) {
                header("User-Agent", "IN2000 Team 16 jostein.ibsen@gmail.com")
            }.bodyAsText()
            println(url)
            Log.d("NIBIO_DEBUG", "NIBIO: \n$responseText")
            parseResponse(responseText)


        } catch (e: Exception) {
            println("Network error SatSkog: ${e.message}")
            null
        }
    }

    private fun parseResponse(gmlText: String): SatSkogResponse {

        if (!gmlText.contains("<alder>")){
            Log.d("NIBIO_DEBUG", "NIBIO: No data on this point")
            return SatSkogResponse()
        }

        fun extractValue(tagName: String): String? {
            val regex = "<$tagName>(.*?)</$tagName>".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = regex.find(gmlText)
            return match?.groupValues?.get(1)?.trim()
        }


        return SatSkogResponse(
            treeSpecies = extractValue("treslag"),
            sprucePct = extractValue("gran_pct")?.toIntOrNull(),
            pinePct = extractValue("furu_pct")?.toIntOrNull(),
            broadleafPct = extractValue("lauv_pct")?.toIntOrNull(),
            fertilityCategory = extractValue("bon1_n"),
            siteIndex = extractValue("bonitet")?.toIntOrNull(),
            standAge = extractValue("alder")?.toIntOrNull(),
            minElevation = extractValue("min_dem")?.toIntOrNull(),
            maxElevation = extractValue("max_dem")?.toIntOrNull()
        )

    }

}

