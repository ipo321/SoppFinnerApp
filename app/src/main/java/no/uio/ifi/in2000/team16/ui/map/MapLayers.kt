package no.uio.ifi.in2000.team16.ui.map

import android.content.Context
import android.content.res.Configuration
import android.view.Gravity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import no.uio.ifi.in2000.team16.R
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression.get
import org.maplibre.android.style.expressions.Expression.interpolate
import org.maplibre.android.style.expressions.Expression.linear
import org.maplibre.android.style.expressions.Expression.rgba
import org.maplibre.android.style.expressions.Expression.stop
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.Property.NONE
import org.maplibre.android.style.layers.Property.VISIBLE
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.layers.PropertyFactory.fillColor
import org.maplibre.android.style.layers.PropertyFactory.fillOpacity
import org.maplibre.android.style.layers.PropertyFactory.fillOutlineColor
import org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.iconAnchor
import org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.iconImage
import org.maplibre.android.style.layers.PropertyFactory.iconSize
import org.maplibre.android.style.layers.PropertyFactory.rasterOpacity
import org.maplibre.android.style.layers.PropertyFactory.visibility
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet

/*
Setting up the map and all the layers of the map in this file

 */


fun isDarkMode(context: Context): Boolean {
    val darkModeFlag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
}

//Sets up the userPosition on the map
fun setupUserLocationLayer(style: Style){
    val source = GeoJsonSource("user-location-source")
    style.addSource(source)

    //Outer ring around user position (Pulsing ring)
    val pulseLayer = CircleLayer("user-location-pulse", "user-location-source")
    pulseLayer.setProperties(
        circleRadius(13f),
        circleColor("#2769DB"),
        circleStrokeWidth(0f),
        circleStrokeColor("#000000")

    )
    style.addLayer(pulseLayer)

    val dotLayer = CircleLayer("user-location-layer", "user-location-source")
    dotLayer.setProperties(
        circleRadius(7f),
        circleColor("#2769DB"),
        circleStrokeWidth(3f),
        circleStrokeColor("#FFFFFF")
    )
    style.addLayer(dotLayer)
}

fun setupPointMarker(style: Style) {
    val source = GeoJsonSource("position-source")
    style.addSource(source)

    val circleLayer = CircleLayer("position-layer", "position-source")
    circleLayer.setProperties(
        circleRadius(6f),
        circleColor("#2769DB"),   // blue like Google Maps
        circleStrokeWidth(1.5f),
        circleStrokeColor("#FFFFFF")
    )
    style.addLayer(circleLayer)

}

fun setupPinIcon(style: Style) {
    val source = GeoJsonSource("clicked-source")
    style.addSource(source)

    val symbolLayer = SymbolLayer("clicked-layer", "clicked-source")
        .withProperties(
            iconImage("pin-icon"),
            iconSize(1.5f), // The size of the pin-icon
            iconAllowOverlap(true),
            iconIgnorePlacement(true),
            iconAnchor(Property.ICON_ANCHOR_BOTTOM) // Makes the pin-tip point where clicked
        )
    style.addLayer(symbolLayer)
}

fun addPinImage(style: Style, context: Context) {
    val drawable = AppCompatResources.getDrawable(context, R.drawable.location_pin)
        ?: return

    val bitmap = drawable.toBitmap()  // Makes the drawable into a bitmap for MapLibre
    style.addImage("pin-icon", bitmap)
}

fun setupForestLayer(style: Style, isVisible: Boolean) {
    val satskogUrl = "https://wms.nibio.no/cgi-bin/satskog" +
            "?SERVICE=WMS" +
            "&VERSION=1.3.0" +
            "&REQUEST=GetMap" +
            "&LAYERS=Dominerende_treslag" +
            "&CRS=EPSG:3857" +
            "&BBOX={bbox-epsg-3857}" +
            "&WIDTH=256" +
            "&HEIGHT=256" +
            "&FORMAT=image/png" +
            "&TRANSPARENT=TRUE"

    val wmsSource = RasterSource(
        "satskog-source",
        TileSet("2.1.0", satskogUrl),
        256
    )
    style.addSource(wmsSource)
    val wmsLayer = RasterLayer("satskog-layer", "satskog-source")
    wmsLayer.setProperties(
        rasterOpacity(0.2f),
        visibility(if (isVisible) VISIBLE else NONE)
    )
    style.addLayer(wmsLayer)
}

fun setupHistoricalFindsLayer(style: Style) {
    val source = GeoJsonSource("historical-finds-source")
    style.addSource(source)

    val circleLayer = CircleLayer("historical-finds-layer", "historical-finds-source")
    circleLayer.setProperties(
        circleRadius(6f),
        circleColor("#D0ECA8"),   // PrimaryFixed
        circleStrokeWidth(1.5f),
        circleStrokeColor("#1B1C15")  //OnSurfaceDark
    )
    style.addLayer(circleLayer)
}

fun setupHeatmapLayer(style: Style) {
    //  Source
    val heatmapSource = GeoJsonSource("heatmap-source")
    style.addSource(heatmapSource)

    // Colour and Size
    val fillLayer = FillLayer("heatmap-layer", "heatmap-source")


    fillLayer.setProperties(
        fillColor(
            interpolate(
                linear(), get("score"),
                stop(0, rgba(248, 113, 113, 0.4f)),     // red
                stop(15, rgba(251, 146, 60, 0.4f)),   // Orange
                stop(50, rgba(250, 204, 21, 0.4f)),// Yellow
                stop(100, rgba(74, 222, 128, 0.4f))  // Green
            )
        ),
        //Changes the border
        fillOutlineColor(rgba(0, 0, 0, 0f)),
        fillOpacity(1f)
    )


    if (style.getLayer("Sand") != null) {
        style.addLayerBelow(fillLayer, "Sand")
    } else {
        style.addLayer(fillLayer)
    }
}

fun setMapLayout(map: MapLibreMap, context: Context) {
    map.cameraPosition = CameraPosition.Builder()
        .target(LatLng(59.91, 10.75)) // Where the map opens, now Oslo
        .zoom(11.0) // Degree of zoom when map is opened
        .build()
    map.setMinZoomPreference(2.0) // Minimum zoom
    map.setMaxZoomPreference(25.0) // Maximum zoom

    val density = context.resources.displayMetrics.density
    val topMargin = (244 * density).toInt() // Transforms dp to pixels
    val rightMargin = (20 * density).toInt()
    val ui = map.uiSettings
    // If we want a compass
    ui.isCompassEnabled = true
    ui.compassGravity = Gravity.TOP or Gravity.END
    ui.setCompassMargins(0, topMargin, rightMargin, 0) // Sets the position of the compass
    // Adds zoom, scroll, rotation, tilt and double tap
    ui.isZoomGesturesEnabled = true
    ui.isScrollGesturesEnabled = true
    ui.isRotateGesturesEnabled = true
    ui.isTiltGesturesEnabled = true
    ui.isDoubleTapGesturesEnabled = true
    ui.isHorizontalScrollGesturesEnabled = true
}