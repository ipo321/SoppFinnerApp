package no.uio.ifi.in2000.team16

import android.annotation.SuppressLint
import android.content.Context
import org.osmdroid.config.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team16.ui.home.HomeScreen
import no.uio.ifi.in2000.team16.ui.home.ModulViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import java.io.File
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.MapTileIndex
import org.osmdroid.util.GeoPoint
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import androidx.compose.ui.platform.LocalContext

/*
    Bruker MapLibre Native som oppdateres jevnlig på github: https://github.com/maplibre/maplibre-native
    Tutorial finnes her: https://maplibre.org/maplibre-native/android/examples/

    Kartet er delvis konfigurert i res/layout/main.xml

    Selve tilsene som viser byer og gater osv er kartet Base fra https://cloud.maptiler.com/maps/

    I emulatoren kan kartet zoomes inn på ved å dobbelt-trykke på skjermen, eller zoome inn og ut
    ved å holde inne Ctrl og dra i kartet.
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapLibre.getInstance(this)

        enableEdgeToEdge()
        setContent {
            Team16Theme {
                //HomeScreen(modulViewModel = hiltViewModel<ModulViewModel>())
                KartScreen()
            }
        }
    }
}

@Composable
fun KartScreen() {
    val kontekst = LocalContext.current
    val livssyklusEier = LocalLifecycleOwner.current
    // Her opprettes kartet med MapView og remember slik at state huskes
    val kartView = remember {
        MapView(kontekst).apply {
            onCreate(null)

            getMapAsync { kart ->
                // Foreløpig bruk er kartet Base fra Maptiler, kan byttes ut
                kart.setStyle("https://api.maptiler.com/maps/base-v4/style.json?key=pzawSSamH6NaI2881UMa") { stil ->
                    // Logging for å se hva som skjer når kartet brukes
                    Log.d("MAP", "Stil lastet: ${stil.uri}")

                    kart.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(59.91, 10.75)) // Utsnittet hvor kartet åpnes. Nå Oslo.
                        .zoom(11.0) // Grad av zoom ved åpning av kart
                        .build()
                    kart.setMinZoomPreference(2.0) // Minimum zoom
                    kart.setMaxZoomPreference(25.0) // Maximum zoom

                    val ui = kart.uiSettings
                    // Hvis vi vil ha med kompass
                    ui.isCompassEnabled = true
                    ui.compassGravity = Gravity.TOP or Gravity.END
                    // Legger til zoom, scrolling, rotering, tilting og dobbelttrykk
                    ui.isZoomGesturesEnabled = true
                    ui.isScrollGesturesEnabled = true
                    ui.isRotateGesturesEnabled = true
                    ui.isTiltGesturesEnabled = true
                    ui.isDoubleTapGesturesEnabled = true
                    ui.isHorizontalScrollGesturesEnabled = true

                }
            }
        }
    }
    // kartViewet får beskjed når livssyklusen endres
    DisposableEffect(livssyklusEier, kartView) {
        // observator lytter til livssyklusen og når hver av funksjonene under kalles på
        // observatoren, så kalles de samme på kartView
        val observator = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                kartView.onStart()
            }
            override fun onResume(owner: LifecycleOwner) {
                kartView.onResume()
            }
            override fun onPause(owner: LifecycleOwner) {
                kartView.onPause()
            }
            override fun onStop(owner: LifecycleOwner) {
                kartView.onStop()
            }
            override fun onDestroy(owner: LifecycleOwner) {
                kartView.onDestroy()
            }
        }
        livssyklusEier.lifecycle.addObserver(observator)
        // Når MapScreen fjernes, fjernes observatoren og mapViewet kaller ikke lenger på funksjonene over
        onDispose {
            livssyklusEier.lifecycle.removeObserver(observator)
        }
    }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { kartView }
    )
}

//@Composable
//fun MapScreen(
//    modifier: Modifier = Modifier
//) {
//    AndroidView(
//        modifier = modifier,
//        factory = { context ->
//            createMapViewWithWms(context)
//        }
//    )
//}
//
//fun createMapViewWithWms(context: Context): MapView {
//    val mapView = MapView(context)
//
//    mapView.setTileSource(TileSourceFactory.MAPNIK) // bakgrunnskart
//
//    val controller = mapView.controller
//    controller.setZoom(5.5)
//    controller.setCenter(GeoPoint(65.0, 15.0))
//
//    val wmsTileSource = WmsTileSource3857(
//        baseUrl = "https://public-victoria.met.no/wms",
//        layers = "topo",
//        format = "image/png"
//    )
//
//    val wmsProvider = MapTileProviderBasic(context, wmsTileSource)
//
//    val wmsOverlay = TilesOverlay(wmsProvider, context)
//    wmsOverlay.loadingBackgroundColor = 0x00000000
//    wmsOverlay.loadingLineColor = 0x00000000.toInt()
//
//    mapView.overlays.add(wmsOverlay)
//
//    mapView.setMultiTouchControls(true)
//    return mapView
//}
//
///*
//    Klasse som forteller hvordan "tiles" (kart-utsnitt) hentes via WMS og
//    3857 er koordinatsystemet som brukes av de fleste webkart.
// */
//class WmsTileSource3857(
//    private val baseUrl: String,
//    private val layers: String,
//    private val format: String
//) : OnlineTileSourceBase(
//    // Zoom- og pixel-nivåer brukt her er typiske verdier
//    "WMS",
//    0,
//    19, // Hvor langt inn vi kan zoome (MÅ TESTES)
//    256,
//    "",
//    arrayOf()
//) {
//    override fun getTileURLString(pMapTileIndex: Long): String {
//        // Henter ut tile-indekser
//        val x = MapTileIndex.getX(pMapTileIndex)
//        val y = MapTileIndex.getY(pMapTileIndex)
//        val z = MapTileIndex.getZoom(pMapTileIndex)
//
//        // Transformerer indeksene til en BBox
//        val bbox = tileToBBox3857(x, y, z)
//
//        // Bygger strengen som brukes for å hente ut WMS-tilen
//        return buildString {
//            append(baseUrl)
//            if (!baseUrl.contains("?")) append("?") else append ("&")
//            append("SERVICE=WMS")
//            append("&REQUEST=GetMap")
//            append("&VERSION=1.3.0")
//            append("&LAYERS=").append(layers)
//            append("&CRS=EPSG:3857")
//            append("&BBOX=").append(bbox)
//            append("&WIDTH=256&HEIGHT=256")
//            append("&FORMAT=").append(format)
//            append("&STYLES=")
//            append("&TRANSPARENT=TRUE")
//            //append("&GROUP=IN2000")
//        }
//    }
//
//    // Transoformerer tile-indeksene til en WMS BBox i koordinatsystemet.
//    // En WMS BBox er området vi skal gi kart over, altså selve kartbildet.
//    private fun tileToBBox3857(x: Int, y: Int, z: Int): String {
//        val tileSize = 256.0 // 256 x 256 pixler
//        val jordOmkrets = 2.0 * Math.PI * 6378137.0 // Formel omkrets: 2πR. 6378137.0 er jordens radius.
//        // Omkrets / tile-størrelse for meter per pixel
//        val initialResolution = jordOmkrets / tileSize
//        // Omkrets / 2 for å midtstille utsnittet ( TROR JEG ? )
//        val originShift = jordOmkrets / 2.0
//        // Oppløsning deles på 2^z (skrives her som bitshift med: 1 shl z)
//        val resolution = initialResolution / (1 shl z)
//
//        val tileWidthMeters = tileSize * resolution
//        val minX = x * tileWidthMeters - originShift // Vest
//        val maxX = (x + 1) * tileWidthMeters - originShift // Øst. x + 1 er helt til høyre i av tilen.
//        val maxY = originShift - y * tileWidthMeters // Nord
//        val minY = originShift - (y + 1) * tileWidthMeters // Sør. y + 1 er helt øverst i tilen.
//
//        return "$minX,$minY,$maxX,$maxY"
//    }
//}

//@Composable
//fun OsmdroidMapScreen() {
//    AndroidView(
//        factory = { context ->
//            val mapView = MapView(context).apply {
//                id = R.id.map
//                setMultiTouchControls(true)
//                setTileSource(TileSourceFactory.MAPNIK)
//
//                controller.setZoom(5.0)
//                controller.setCenter(GeoPoint(64.0, 11.0))
//            }
//            addGeoNorgeWmsOverlay(mapView, context)
//            mapView
//        },
//        update = { mapView ->
//
//        },
//        modifier = Modifier.fillMaxSize()
//    )
//}

//fun addGeoNorgeWmsOverlay(mapView: MapView, context: Context) {
//    val wmsBaseUrl = "https://wms.geonorge.no/skwms1/wms.kartdata?"
//    val wmsTileSource = object : OnlineTileSourceBase(
//        "GeoNorgeWms",
//        0, 18,
//        256, "",
//        arrayOf(wmsBaseUrl)
//    ) {
//        override fun getTileUrlString(pMapTileIndex: Long): String {
//            val x = MapTileIndex.getX(pMapTileIndex)
//            val y = MapTileIndex.getY(pMapTileIndex)
//            val z = MapTileIndex.getZoom(pMapTileIndex)
//
//            val bbox = getBoundingBox3857(x, y, z)
//
//            val layerName = "N5000Arealdekkeflate" // fyll inn
//
//            return (wmsBaseUrl +
//                    "SERVICE=WMS&" +
//                    "VERSION=1.3.0&" +
//                    "REQUEST=GetMap&" +
//                    "LAYERS=$layerName&" +
//                    "STYLES=&" +
//                    "CRS=EPSG:3857&" +
//                    "BBOX=${bbox.lonWest},${bbox.latSouth},${bbox.lonEast},${bbox.latNorth}&" +
//                    "WIDTH=256&" +
//                    "HEIGHT=256&" +
//                    "FORMAT=image/png&" +
//                    "TRANSPARENT=true")
//        }
//    }
//
//    val provider = MapTileProviderBasic(context)
//    provider.tileSource = wmsTileSource
//    val wmsOverlay = TilesOverlay(provider, context)
//    wmsOverlay.loadingBackgroundColor = 0x00000000
//    wmsOverlay.loadingLineColor = 0x00000000.toInt()
//
//    mapView.overlays.add(wmsOverlay)
//}
//
//fun getBoundingBox3857(x: Int, y: Int, zoom: Int): BoundingBox {
//    val tileSize = 256.0
//    val initialResolution = 2.0 * PI * 6378137.0 / tileSize
//    val originShift = 2.0 * PI * 6378137.0 / 2.0
//
//    val resolution = initialResolution / 2.0.pow(zoom.toDouble())
//
//    val maxY = originShift - y * tileSize * resolution
//    val minX = x * tileSize * resolution - originShift
//    val minY = originShift - (y + 1) * tileSize * resolution
//    val maxX = (x + 1) * tileSize * resolution - originShift
//
//    return BoundingBox(maxY, minX, minY, maxX)
//}