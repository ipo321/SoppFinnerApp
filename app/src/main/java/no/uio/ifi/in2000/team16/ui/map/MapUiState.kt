package no.uio.ifi.in2000.team16.ui.map

import no.uio.ifi.in2000.team16.data.kartverket.Address
import no.uio.ifi.in2000.team16.data.kartverket.SearchableLocation
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.HeatmapTile
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import org.maplibre.android.geometry.LatLng

/*
    General class for UiStates
*/

data class MapScreenState(
    val screenStatus: ScreenStatus = ScreenStatus.Idle,
    val selectedMushroom: Mushroom? = null,
    val currentContext: EnvironmentContext? = null,
    val evaluation: EvaluationResult? = null,
    val favoriteMushrooms: List<Mushroom> = emptyList(),
    val isHeatmapEnabled: Boolean = false,
    val heatmapTiles: List<HeatmapTile> = emptyList(),
    val isFetchingHeatmap: Boolean = false,
    val searchQuery: String = "",
    val isHistoricalFindsEnabled: Boolean = false,
    val currentTileSize: Double = 0.2,
    val isSatelliteMode: Boolean = false,
    val showWmsForest: Boolean = false,
    val lastClickedLat: Double? = null,
    val lastClickedLon: Double? = null,
    val searchSuggestions: List<SearchableLocation> = emptyList(),
    val addressSuggestions: List<Address> = emptyList(),
    val userLocation: LatLng? = null,
    val moveCameraToUser: Boolean = false
)


sealed class ScreenStatus{
    object Idle : ScreenStatus()
    object Loading : ScreenStatus()
    object Success : ScreenStatus()
    data class Error(val message: String) : ScreenStatus()
}
