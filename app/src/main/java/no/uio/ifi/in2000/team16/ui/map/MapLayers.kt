package no.uio.ifi.in2000.team16.ui.map

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.data.artsdatabasen.Occurrence
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.expressions.Expression.color
import org.maplibre.android.style.expressions.Expression.get
import org.maplibre.android.style.expressions.Expression.has
import org.maplibre.android.style.expressions.Expression.interpolate
import org.maplibre.android.style.expressions.Expression.linear
import org.maplibre.android.style.expressions.Expression.not
import org.maplibre.android.style.expressions.Expression.rgba
import org.maplibre.android.style.expressions.Expression.step
import org.maplibre.android.style.expressions.Expression.stop
import org.maplibre.android.style.expressions.Expression.zoom
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.Property.NONE
import org.maplibre.android.style.layers.Property.VISIBLE
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleOpacity
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
import org.maplibre.android.style.layers.PropertyFactory.textAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.textColor
import org.maplibre.android.style.layers.PropertyFactory.textField
import org.maplibre.android.style.layers.PropertyFactory.textIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.textSize
import org.maplibre.android.style.layers.PropertyFactory.visibility
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonOptions
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

/*
 * MapLayers.kt
 *
 * Contains all MapLibre layer and source setup for the map screen.
 * Each function adds one logical piece to the map style:
 *
 *  - setupUserLocationLayer   : blue dot showing the user's current position
 *  - setupPinIcon / addPinImage: pin marker placed on user tap
 *  - setupForestLayer         : NIBIO Satskog tree-species WMS overlay
 *  - setupHistoricalFindsLayer: clustered orange circles for historical mushroom finds
 *  - setupHeatmapLayer        : colored polygons showing mushroom-suitability scores
 *  - setMapLayout             : zoom limits and gesture settings
 *
 * Layers share GeoJSON sources that start empty — data is pushed in at runtime
 * by the ViewModel using the source IDs exported from this file.
 */


// Colors used for historical-find markers and their clusters.
val HistoricalFindFill = Color(0xFFFF9800)
val HistoricalFindStroke = Color(0xFF1B1C15)
val ClusterColorSmall = Color(0xFFFFCC80)  // 1–9 finds
val ClusterColorMedium = Color(0xFFFF9800) // 10–49 finds
val ClusterColorLarge = Color(0xFFE65100)    // 50+ finds

// IDs used to identify the source and the three layers that make up
// the historical-finds visualization (single points, cluster bubbles, count labels).
// HISTORICAL_FINDS_SOURCE_ID is the ID of the GeoJSON source
// that holds all the find points. The three layers below all read from this one source
const val HISTORICAL_FINDS_SOURCE_ID = "historical-finds-source"
private const val HISTORICAL_FINDS_LAYER_ID = "historical-finds-layer"
private const val HISTORICAL_FINDS_CLUSTERS_ID = "historical-finds-clusters"
private const val HISTORICAL_FINDS_CLUSTER_COUNT_ID = "historical-finds-cluster-count"

// MapLibre automatically adds a "point_count" property to clustered features.
// We use it both to filter clusters vs. individual points and to size/color the bubbles.
private const val PROP_POINT_COUNT = "point_count"

// Clustering tuning: stop clustering above zoom 13 (so individual finds appear when zoomed in),
// group points within 30dp of each other, and switch cluster styling at 10 and 50 points.
private const val CLUSTER_MAX_ZOOM = 13
private const val CLUSTER_RADIUS_DP = 30
private const val CLUSTER_BREAKPOINT_MEDIUM = 10
private const val CLUSTER_BREAKPOINT_LARGE = 50


/**
 * Adds the user-location source and its two stacked circle layers to [style].
 *
 * The result is the classic blue dot: a larger pulse ring behind a smaller solid dot.
 * The source starts empty — the actual coordinate is pushed in later as the
 * location updates.
 */
fun setupUserLocationLayer(style: Style){
    val source = GeoJsonSource("user-location-source")
    style.addSource(source)

    // Outer ring around user position (Pulsing ring)
    val pulseLayer = CircleLayer("user-location-pulse", "user-location-source")
    pulseLayer.setProperties(
        circleRadius(13f),
        circleColor("#2769DB"),
        circleStrokeWidth(0f),
        circleStrokeColor("#000000")

    )
    style.addLayer(pulseLayer)

    // Inner solid dot drawn on top of the pulse ring.
    val dotLayer = CircleLayer("user-location-layer", "user-location-source")
    dotLayer.setProperties(
        circleRadius(7f),
        circleColor("#2769DB"),
        circleStrokeWidth(3f),
        circleStrokeColor("#FFFFFF")
    )
    style.addLayer(dotLayer)
}

/**
 * Adds an empty source plus a [SymbolLayer] that renders the "pin-icon" image.
 *
 * Used to show where the user has tapped on the map. The pin's position is
 * updated by feeding GeoJSON into the `clicked-source` source.
 *
 * @see addPinImage which must be called first to register the icon bitmap.
 */
fun setupPinIcon(style: Style) {
    val source = GeoJsonSource("clicked-source")
    style.addSource(source)

    val symbolLayer = SymbolLayer("clicked-layer", "clicked-source")
        .withProperties(
            iconImage("pin-icon"),
            iconSize(1.5f), // The size of the pin-icon
            iconAllowOverlap(true),     // Always render, even if it overlaps another icon.
            iconIgnorePlacement(true),  // Don't let this pin block placement of other icons.
            iconAnchor(Property.ICON_ANCHOR_BOTTOM) // Makes the pin-tip point where clicked
        )
    style.addLayer(symbolLayer)
}

/**
 * Loads the location-pin drawable and registers it with [style] as `"pin-icon"`.
 *
 * Must be called before [setupPinIcon] uses the icon. Silently returns if the
 * drawable can't be resolved.
 */
fun addPinImage(style: Style, context: Context) {
    val drawable = AppCompatResources.getDrawable(context, R.drawable.location_pin)
        ?: return

    val bitmap = drawable.toBitmap()  // Makes the drawable into a bitmap for MapLibre
    style.addImage("pin-icon", bitmap)
}

/**
 * Adds the NIBIO "Satskog" dominant-tree-species WMS as a semi-transparent raster overlay.
 *
 * The `{bbox-epsg-3857}` placeholder in the URL is filled in by MapLibre per tile request.
 *
 * @param isVisible initial visibility — lets the layer be toggled from the UI without
 * re-adding it to the style.
 */
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
        rasterOpacity(0.2f),  // Faint overlay so the basemap underneath is still readable.
        visibility(if (isVisible) VISIBLE else NONE)
    )
    style.addLayer(wmsLayer)
}

/**
 * Sets up the layers that render historical mushroom finds with clustering.
 *
 * One [GeoJsonSource] is configured with clustering enabled, then three layers
 * draw it:
 *  1. individual finds — non-clustered points,
 *  2. cluster bubbles — circles whose size and color step up with point count,
 *  3. cluster counts — the number drawn inside each bubble.
 *
 * The cluster radius is given in dp and scaled by display density so it looks
 * the same on phones with different DPI.
 */
fun setupHistoricalFindsLayer(style: Style, context: Context) {
    // Cluster radius is given in pixels; multiply by display density so it
    // stays visually consistent across phones with different DPI.
    val density = context.resources.displayMetrics.density

    style.addSource(
        GeoJsonSource(
            HISTORICAL_FINDS_SOURCE_ID,
            GeoJsonOptions()
                .withCluster(true)
                .withClusterMaxZoom(CLUSTER_MAX_ZOOM)
                .withClusterRadius((CLUSTER_RADIUS_DP * density).toInt())
        )
    )


    style.addLayer(buildIndividualFindLayer())
    style.addLayer(buildClusterBubbleLayer())
    style.addLayer(buildClusterCountLayer())//must be last so it renders on top of bubble Layer
}

/**
 * Builds the layer that draws non-clustered finds.
 *
 * Filtered to features that don't have a `point_count` property. The radius
 * scales with zoom (linear interpolation between zooms 8, 14 and 18) so dots
 * stay tappable when zoomed out without dominating the map when zoomed in.
 */
private fun buildIndividualFindLayer(): CircleLayer =
    CircleLayer(HISTORICAL_FINDS_LAYER_ID, HISTORICAL_FINDS_SOURCE_ID)
        .withProperties(
            circleRadius(
                interpolate(
                    linear(), zoom(),
                    stop(8, 3f),
                    stop(14, 6f),
                    stop(18, 10f)
                )
            ),
            circleColor(HistoricalFindFill.toArgb()),
            circleStrokeWidth(1f),
            circleStrokeColor(HistoricalFindStroke.toArgb()),
        )
        .withFilter(not(has(PROP_POINT_COUNT)))

/**
 * Builds the layer that draws cluster bubbles.
 *
 * Filtered to features that *do* have a `point_count` property. Both the radius
 * and color step up at [CLUSTER_BREAKPOINT_MEDIUM] and [CLUSTER_BREAKPOINT_LARGE],
 * giving a quick visual sense of how many finds are grouped.
 */
private fun buildClusterBubbleLayer(): CircleLayer =
    CircleLayer(HISTORICAL_FINDS_CLUSTERS_ID, HISTORICAL_FINDS_SOURCE_ID)
        .withProperties(
            circleRadius(
                step(
                    get(PROP_POINT_COUNT),
                    15f, // default radius for clusters below the medium breakpoint
                    stop(CLUSTER_BREAKPOINT_MEDIUM, 20f),
                    stop(CLUSTER_BREAKPOINT_LARGE, 30f)
                )
            ),
            circleColor(
                step(
                    get(PROP_POINT_COUNT),
                    color(ClusterColorSmall.toArgb()),
                    stop(CLUSTER_BREAKPOINT_MEDIUM, color(ClusterColorMedium.toArgb())),
                    stop(CLUSTER_BREAKPOINT_LARGE, color(ClusterColorLarge.toArgb()))
                )
            ),
            circleOpacity(0.7f),

            )
        .withFilter(has(PROP_POINT_COUNT))

/**
 * Builds the symbol layer that draws the count number inside each cluster bubble.
 *
 * `textAllowOverlap` and `textIgnorePlacement` are both `true` so the count is
 * always shown, even when other map labels would otherwise hide it.
 */
private fun buildClusterCountLayer(): SymbolLayer =
    SymbolLayer(HISTORICAL_FINDS_CLUSTER_COUNT_ID, HISTORICAL_FINDS_SOURCE_ID)
        .withProperties(
            textField(Expression.toString(get(PROP_POINT_COUNT))),
            textSize(16f),
            textColor(HistoricalFindStroke.toArgb()),
            textIgnorePlacement(true),
            textAllowOverlap(true),
        )
        .withFilter(has(PROP_POINT_COUNT))

/**
 * Adds a polygon fill layer that visualizes mushroom-suitability scores.
 *
 * Each feature is expected to carry a `score` property in the range 0–100.
 * The score is mapped to a color gradient: red (poor) → orange → yellow → green (good).
 *
 * The layer is inserted *below* the basemap's `"Sand"` layer when present so that
 * roads and labels stay readable on top of the colored polygons; otherwise it is
 * added on top.
 */
fun setupHeatmapLayer(style: Style) {
    //  Source
    val heatmapSource = GeoJsonSource("heatmap-source")
    style.addSource(heatmapSource)

    // Color and Size
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
        fillOutlineColor(rgba(0, 0, 0, 0f)),  // Fully transparent outline — no visible border.
        fillOpacity(1f)
    )


    // Insert the heatmap below the basemap's "Sand" layer if it exists,
    // so labels and roads stay on top of the colored polygons.
    // Fall back to adding it on top if the basemap doesn't include "Sand".
    if (style.getLayer("Sand") != null) {
        style.addLayerBelow(fillLayer, "Sand")
    } else {
        style.addLayer(fillLayer)
    }
}

fun setMapLayout(map: MapLibreMap) {
    map.setMinZoomPreference(2.0) // Minimum zoom
    map.setMaxZoomPreference(25.0) // Maximum zoom
    val ui = map.uiSettings
    // Adds zoom, scroll, rotation, tilt and double tap
    ui.isCompassEnabled = false
    ui.isZoomGesturesEnabled = true
    ui.isScrollGesturesEnabled = true
    ui.isRotateGesturesEnabled = true
    ui.isTiltGesturesEnabled = true
    ui.isDoubleTapGesturesEnabled = true
    ui.isHorizontalScrollGesturesEnabled = true
}

/**
 * Takes the global list of mushroom from the ViewModel and updates the map.
 * MapLibre automatically does most of the things.
 */

fun Style.updateHistoricalFindsPins(markers: List<Occurrence>, isEnabled: Boolean) {
    val source = getSourceAs<GeoJsonSource>(HISTORICAL_FINDS_SOURCE_ID) ?: return
    if (!isEnabled || markers.isEmpty()) {
        source.setGeoJson(FeatureCollection.fromFeatures(emptyList()))
        return
    }
    val features = markers
        .filter { it.decimalLatitude != null && it.decimalLongitude != null }
        .map { occ ->
            Feature.fromGeometry(
                Point.fromLngLat(occ.decimalLongitude!!, occ.decimalLatitude!!)
            )
        }
    source.setGeoJson(FeatureCollection.fromFeatures(features))
}

