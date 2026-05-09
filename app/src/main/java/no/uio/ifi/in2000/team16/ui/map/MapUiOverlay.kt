package no.uio.ifi.in2000.team16.ui.map

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.ui.common.MushroomImage
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme.padding

/**
    Smaller elements on the map like the center button, loading indicators and so on
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapComponents(
    viewModel: MapViewModel,
    uiState: MapScreenState,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(padding.outer),
            verticalAlignment = Alignment.Top,
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    top = padding.outer,
                    start = padding.outer,
                    end = padding.outer
                )
                .semantics { isTraversalGroup = true }
        ) {
            MapMenu(
                viewModel = viewModel,
                uiState = uiState,
                menuExpanded = menuExpanded,
                onClick = {
                    menuExpanded = !menuExpanded
                    viewModel.setSearchBarExpanded(false)
                },
                onDismissRequest = { menuExpanded = false },
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
                    top = padding.outer,
                    end = padding.outer
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
                .padding(end = padding.outer)
        ) {
            CentralizeMap(
                onClick = { viewModel.centerOnUser()}
            )
        }
    }
}

@Composable
fun MapMenu(
    viewModel: MapViewModel,
    uiState: MapScreenState,
    menuExpanded: Boolean,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(padding.mapButtonsSize)
            .shadow(
                elevation = padding.shadowElevationSmall,
                shape = CircleShape,
                clip = false
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            ),
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
            onToggleHeatMap = { viewModel.toggleHeatmap() }
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
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DockedSearchBar(
        modifier = modifier
            .fillMaxWidth()
            .semantics { traversalIndex = 0f },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        shape = if (searchExpanded) RoundedCornerShape(padding.inner) else CircleShape,
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
                    viewModel.searchAddressByText(query)
                    onSearch(textFieldState.text.toString())
                    onExpandedChange(false)
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
        shadowElevation = padding.shadowElevationSmall,
        tonalElevation = padding.noPadding
    ) {
        // Displays search results in a scrollable column
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = padding.searchResultHeight) // Max height of search result list
        ) {
            items(uiState.searchSuggestions) { suggestion ->
                ListItem(
                    headlineContent = { Text(suggestion.title) },
                    supportingContent = { Text(suggestion.subtitle) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.searchLocation(suggestion)
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

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.background,
        shadowElevation = padding.shadowElevationSmall,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable {
                    expanded = !expanded
                }
                .padding(padding.inner)
        ) {
            if (expanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                        contentDescription = "Pil fremover",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(padding.inner)
                    ) {
                        Text(
                            text = stringResource(R.string.viser_resultater_for),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = selected.commonName,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Column {
                MushroomImage(
                    selected = selected,
                    size = padding.mapButtonsSize,
                    background = MaterialTheme.colorScheme.background
                )
            }
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
            .padding(top = padding.outer)
            .size(padding.mapButtonsSize)
            .shadow(
                elevation = padding.shadowElevationSmall,
                shape = CircleShape,
                clip = false
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            ),
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
    Column {
        repeat(numberOfRows) { index ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(padding.inner),
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        start = padding.outer,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(padding.colorExplanationSize)
                        .background(colors[index])
                )
                Text(
                    text = text[index],
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
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
