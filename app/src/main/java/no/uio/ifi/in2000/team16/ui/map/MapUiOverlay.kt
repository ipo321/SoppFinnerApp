package no.uio.ifi.in2000.team16.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.BoundingBox
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.ui.common.MushroomImage
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

/*
Smaller elements on hte map like the center button, loading indicators and so on

 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapComponents(
    viewModel: MapViewModel,
    uiState: MapScreenState,
    mapView: MapView,
    onNavigateToDev: () -> Unit,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    Column() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Team16Theme.padding.outer),
            verticalAlignment = Alignment.Top,
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    top = Team16Theme.padding.outer,
                    start = Team16Theme.padding.outer,
                    end = Team16Theme.padding.outer
                )
                .semantics { isTraversalGroup = true }
        ) {
            MapMenu(
                viewModel = viewModel,
                uiState = uiState,
                mapView = mapView,
                menuExpanded = menuExpanded,
                onClick = {
                    menuExpanded = !menuExpanded
                    viewModel.setSearchBarExpanded(false)
                },
                onDismissRequest = { menuExpanded = false },
                onNavigateToDev = onNavigateToDev
            )
            MapSearchBar(
                viewModel = viewModel,
                uiState = uiState,
                searchExpanded = uiState.isSearchBarExpanded,
                onExpandedChange = { expanded ->
                    viewModel.setSearchBarExpanded(expanded)
                    if (expanded) {
                        viewModel.dismissEvaluation()
                    }
                },
                mapView = mapView,
                textFieldState = textFieldState,
                onSearch = onSearch,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top,
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    top = Team16Theme.padding.outer,
                    end = Team16Theme.padding.outer
                )
        ) {
            MushroomIcon(
                mushroom = uiState.selectedMushroom
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(end = Team16Theme.padding.outer)
        ) {
            CentralizeMap(
                onClick = {
                    val userLocation = uiState.userLocation
                    if (userLocation != null) {
                        mapView.getMapAsync { map ->
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(userLocation.latitude, userLocation.longitude),
                                    13.0
                                )
                            )
                        }
                    }
                    viewModel.setSearchBarExpanded(false)
                }
            )
        }
    }
}

@Composable
fun MapMenu(
    viewModel: MapViewModel,
    uiState: MapScreenState,
    mapView: MapView,
    menuExpanded: Boolean,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onNavigateToDev: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(Team16Theme.padding.mapButtonsSize)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            ),
//        .border(
//            width = Team16Theme.padding.borderWidthSmall,
//            color = MaterialTheme.colorScheme.primaryContainer,
//            shape = CircleShape
//        ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.meny),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Dropdown(
            expanded = menuExpanded,
            onDismissRequest = onDismissRequest,
            onNavigateToDev = onNavigateToDev,
            isHeatmapEnabled = uiState.isHeatmapEnabled,
            isHistoricalFindsEnabled = uiState.isHistoricalFindsEnabled,
            onToggleHistoricalFinds = { viewModel.toggleHistoricalFinds() },
            isSatelliteMode = uiState.isSatelliteMode,
            onToggleSatellite = { viewModel.toggleSatellite() },
            showWmsForest = uiState.showWmsForest,
            onToggleWmsForest = { viewModel.toggleWmsForest() },
            favoriteMushrooms = uiState.favoriteMushrooms,
            selectedMushroom = uiState.selectedMushroom,
            onMushroomSelected = { mushroom ->
                viewModel.onMushroomSelected(
                    mushroom
                )
            },
            onToggleHeatMap = {
                mapView.getMapAsync { map ->
                    val bounds = map.projection.visibleRegion.latLngBounds
                    val currentBox = BoundingBox(
                        north = bounds.northEast.latitude,
                        south = bounds.southWest.latitude,
                        east = bounds.northEast.longitude,
                        west = bounds.southWest.longitude
                    )
                    viewModel.toggleHeatmap(currentBox)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchBar(
    viewModel: MapViewModel,
    uiState: MapScreenState,
    searchExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    mapView: MapView,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DockedSearchBar(
        modifier = modifier
            .fillMaxWidth()
            .semantics { traversalIndex = 0f },
//            .border(
//                width = Team16Theme.padding.borderWidthSmall,
//                color = MaterialTheme.colorScheme.primaryContainer,
//                shape = if (searchExpanded) RoundedCornerShape(Team16Theme.padding.inner) else CircleShape
//            ),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        shape = if (searchExpanded) RoundedCornerShape(Team16Theme.padding.inner) else CircleShape,
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.fillMaxWidth(),
                query = textFieldState.text.toString(),
                onQueryChange = {
                    textFieldState.edit { replace(0, length, it) }
                    viewModel.onSearchQueryChanged(it) // Collects suggestions while typing
                    onExpandedChange(it.isNotEmpty())
                },
                onSearch = { query ->
                    viewModel.searchAddressByText(query) { lat, lon ->
                        mapView.getMapAsync { map ->
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 15.0)
                            )
                        }
                    }
                    onSearch(textFieldState.text.toString())
                    onExpandedChange(false) // closes when search is entered/completed
                },
                expanded = searchExpanded,
                onExpandedChange = onExpandedChange,
                placeholder = { Text(stringResource(R.string.sok_i_kartet)) },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textFieldState.edit { replace(0, length, "") }
                                viewModel.onSearchQueryChanged("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.tom_sok),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    else {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.sok)
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                )
            )
        },
        expanded = searchExpanded,
        onExpandedChange = onExpandedChange,
        shadowElevation = Team16Theme.padding.noPadding,
        tonalElevation = Team16Theme.padding.noPadding
    ) {
        // Displays search results in a scrollable column
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = Team16Theme.padding.searchResultHeight) // Max height of search result list
        ) {
            items(uiState.searchSuggestions) { suggestion ->
                ListItem(
                    headlineContent = { Text(suggestion.title) },
                    supportingContent = { Text(suggestion.subtitle) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { // each item is clickable
                            viewModel.searchLocation(suggestion) { lat, lon ->
                                mapView.getMapAsync { map ->
                                    map.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(lat, lon),
                                            13.0 // 13 is how close we want the zoom after searching
                                        )
                                    )
                                }
                            }
                            // updates the searchbar text
                            textFieldState.edit { replace(0, length, suggestion.title) }
                            onExpandedChange(false)
                        }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun MushroomIcon(
    mushroom: Mushroom?,
    modifier: Modifier = Modifier
) {
    val selected = mushroom ?: return
    var expanded by rememberSaveable { mutableStateOf(true) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .clickable {
                expanded = !expanded
            }
//            .border(
//                width = Team16Theme.padding.borderWidthSmall,
//                color = MaterialTheme.colorScheme.primaryContainer,
//                shape = CircleShape
//            )
            .padding(Team16Theme.padding.inner)
    ) {
        if (expanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowForwardIos,
                    contentDescription = "Pil fremover"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(Team16Theme.padding.inner)
                ) {
                    Text(
                        text = stringResource(R.string.viser_resultater_for),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = selected.commonName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Column() {
            MushroomImage(
                selected = selected,
                size = Team16Theme.padding.mapButtonsSize,
                background = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
fun CentralizeMap(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(top = Team16Theme.padding.outer)
            .size(Team16Theme.padding.mapButtonsSize)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            ),
//            .border(
//                width = Team16Theme.padding.borderWidthSmall,
//                color = MaterialTheme.colorScheme.primaryContainer,
//                shape = CircleShape
//            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = stringResource(R.string.sentraliser_kartet),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}



@Composable
fun ColorRow(
    numberOfRows: Int,
    colors: List<Color>,
    text: List<String>,
    modifier: Modifier = Modifier
) {
    Column() {
        repeat(numberOfRows) { index ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(Team16Theme.padding.inner),
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        start = Team16Theme.padding.outer,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(Team16Theme.padding.colorExplanationSize)
                        .background(colors[index])
                )
                Text(
                    text = text[index],
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ColorRowPreview() {
    ColorRow(
        numberOfRows = 3,
        colors = listOf(Color.Red, Color.Blue, Color.Yellow),
        text = listOf("Red", "Blue", "Yellow")
    )
}
