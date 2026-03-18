package no.uio.ifi.in2000.team16.data

data class GeoJSonLayer (
    val asset: String,
    val sourceId: String,
    val layerId: String,
    val color: String
)

// Defines the list of layers we want on the map.
// Hard coding this here is probably not the best idea, but it works for now
fun layerList(): List<GeoJSonLayer> {
    return listOf(
        GeoJSonLayer(
            asset = "verdiklasser_ar5_oslo.geojson",
            sourceId = "verdiklasser_ar5_oslo-surce",
            layerId = "verdiklasser_ar5_oslo-layer",
            color = "#33AA33" // Green
        ),
        GeoJSonLayer(
            asset = "verdiklasser_ar5_baerum.geojson",
            sourceId = "verdiklasser_ar5_baerum-surce",
            layerId = "verdiklasser_ar5_baerum-layer",
            color = "#33AA33" // Green
        ),
        GeoJSonLayer(
            asset = "verdiklasser_ar5_asker.geojson",
            sourceId = "verdiklasser_ar5_asker-surce",
            layerId = "verdiklasser_ar5_asker-layer",
            color = "#33AA33" // Green
        )
    )
}