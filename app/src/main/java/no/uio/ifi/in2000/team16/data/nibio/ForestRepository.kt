package no.uio.ifi.in2000.team16.data.nibio

import android.util.Log
import no.uio.ifi.in2000.team16.domain.model.AreaResource
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import javax.inject.Inject

/*
    Uses Jsoup library to parse html into Json objects. This is necessary because the Nibio
    GetFeatureInfo format doesn't support application/json, only text/html.
 */
class ForestRepository @Inject constructor(
    private val forestDataSource: ForestDataSource
) {
    suspend fun getForestInfo(
        pxX: Int,
        pxY: Int,
        south: Double,
        west: Double,
        north: Double,
        east: Double,
        width: Int,
        height: Int
    ): AreaResource {
        val url = buildGetForestInfoUrl(pxX, pxY, south, west, north, east, width, height)
        Log.d("WMS", "GetFeatureInfo URL: https://wms.nibio.no/cgi-bin/ar5$url")
        val html: String = forestDataSource.getForestInfo(url)
        val document = Jsoup.parse(html) // Parses the html string into a document
        val rows = document.select("table tr") // Finds all table rows

        // Unsure if we need all these values. Can remove those not needed in the future.
        // Also, the translation might be altered if there are better terms
        return try {
            val acres = getValue(rows, "Areal (dekar)")
            val areaType = getValue(rows, "Arealtype")
            val woodSpecies = getValue(rows, "Treslag")
            val forestQuality = getValue(rows, "Skogbonitet")
            val groundConditions = getValue(rows, "Grunnforhold")
            val verificationDate = getValue(rows, "Verifiseringsdato")
            val dataCaptureDate = getValue(rows, "Datafangstdato")
            // Makes the AreaResource object
            AreaResource(
                acres = acres,
                areaType = areaType,
                woodSpecies = woodSpecies,
                forestQuality = forestQuality,
                groundConditions = groundConditions,
                verificationDate = verificationDate,
                dataCaptureDate = dataCaptureDate
            )
        } catch (e: Exception) {
            Log.e("ForestRepository", "Error while parsing HTML", e)
            throw e
        }
    }

    // Gets the value that matches the label
    fun getValue(rows: Elements, label: String): String {
        // Iterates all rows and finds the first string that matches the label
        val row = rows.firstOrNull { tr ->
            val firstCellText = tr.selectFirst("td")?.text()?.trim()
            firstCellText == label
        } ?: error("Fant ikke rad med label: $label")

        // Finds the value that belongs to this label
        return row.select("td").getOrNull(1)?.text()?.trim()
            ?: error("Fant ingen verdi for label $label")
    }

    fun buildGetForestInfoUrl(
        pxX: Int,
        pxY: Int,
        south: Double,
        west: Double,
        north: Double,
        east: Double,
        width: Int,
        height: Int
    ): String {
        return "?language=nor" +
                "&SERVICE=WMS" +
                "&VERSION=1.3.0" +
                "&REQUEST=GetFeatureInfo" +
                "&LAYERS=Arealtype" +
                "&QUERY_LAYERS=Arealtype" +
                "&CRS=EPSG:4326" +
                "&BBOX=$south,$west,$north,$east" +
                "&WIDTH=$width" +
                "&HEIGHT=$height" +
                "&I=$pxX" +
                "&J=$pxY" +
                "&INFO_FORMAT=text/html"
    }
}

