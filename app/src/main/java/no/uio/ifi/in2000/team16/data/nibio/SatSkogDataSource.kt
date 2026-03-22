package no.uio.ifi.in2000.team16.data.nibio

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
        val latMin = lat - 0.001
        val lonMin = lon - 0.001
        val latMax = lat + 0.001
        val lonMax = lon + 0.001

        //Making the url
        val url = "https://wms.nibio.no/cgi-bin/sat_skog" +
                "?SERVICE=WMS" +
                "&VERSION=1.3.0" +
                "&REQUEST=GetFeatureInfo" +
                "&BBOX=$latMin,$lonMin,$latMax,$lonMax" +
                "&CRS=EPSG:4326" +
                "&WIDTH=100&HEIGHT=100" +
                "&LAYERS=sat_skog" +
                "&QUERY_LAYERS=sat_skog" +
                "&I=50&J=50" +
                "&INFO_FORMAT=text/plain"

        return try {
            val responseText = httpClient.get(url) {
                header("User-Agent", "IN2000 Team 16 jostein.ibsen@gmail.com")
            }.bodyAsText()

            parsePlainTextResponse(responseText)

        } catch (e: Exception) {
            println("Network error SatSkog: ${e.message}")
            null
        }
    }

    private fun parsePlainTextResponse(text: String): SatSkogResponse {
        val dataMap = text.lines()
            .filter { it.contains("=") }
            .associate { line ->
                val parts = line.split("=")
                parts[0].trim().lowercase() to parts[1].trim()
            }


        return SatSkogResponse(
            treeSpecies = dataMap["treslag"],
            sprucePct = dataMap["gran_pct"]?.toIntOrNull(),
            pinePct = dataMap["furu_pct"]?.toIntOrNull(),
            broadleafPct = dataMap["lauv_pct"]?.toIntOrNull(),
            fertilityCategory = dataMap["bon1_n"],
            siteIndex = dataMap["bonitet"]?.toIntOrNull(),
            standAge = dataMap["alder"]?.toIntOrNull(),
            minElevation = dataMap["min_dem"]?.toIntOrNull(),
            maxElevation = dataMap["max_dem"]?.toIntOrNull()
        )

    }

}

