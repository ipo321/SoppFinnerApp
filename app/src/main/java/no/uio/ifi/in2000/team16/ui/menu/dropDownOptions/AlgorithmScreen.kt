package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team16.ui.map.MapViewModel
import no.uio.ifi.in2000.team16.ui.menu.AlgorithmUiState
import no.uio.ifi.in2000.team16.ui.menu.AlgorithmViewModel
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme.padding

@Composable
fun AlgorithmScreen(
    mapViewModel: MapViewModel,
    algorithmViewModel: AlgorithmViewModel,
    onNavigateBack: () -> Unit
) {
    var isSelected by remember { mutableStateOf(false) }
    val alUiState = algorithmViewModel.uiState.collectAsState()
    val mapUiState = mapViewModel.uiState.collectAsState()
    val lat = mapUiState.value.lastClickedLat
    val lon = mapUiState.value.lastClickedLon

    FromMenuDefaultScreen(
        header = "Velg algoritme for beregning",
        body = "Velg",
        onNavigateBack = onNavigateBack,
        content = { AlgorithmScreenContent(
            uiState = alUiState,
            onClick = {
                algorithmViewModel.onAlgorithmSelected(it)
                if (lat != null && lon != null) mapViewModel.onMapClicked(lat, lon)
                isSelected != isSelected
            },
            isSelected = isSelected
        ) }
    )
}

@Composable
fun ColumnScope.AlgorithmScreenContent(
    uiState: State<AlgorithmUiState>,
    isSelected: Boolean,
    onClick: (String) -> Unit
) {
    val algorithms = uiState.value.availableAlgorithms
    algorithms.forEach { algorithm ->
        AlgorithmCard(
            algorithm = algorithm,
            onClick = onClick,
            isSelected = isSelected
        )
    }
}

@Composable
fun AlgorithmCard(
    algorithm: String,
    isSelected: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shadowElevation = padding.shadowElevationSmall,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .algorithmCard(isSelected)
                //.clickable { onClick(algorithm) }
                .padding(padding.outer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = algorithm,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Checkbox selects this algorithm for the calculation
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick(algorithm) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

@Composable
private fun Modifier.algorithmCard(isSelected: Boolean): Modifier {
    val borderWidth = if (isSelected) 4.dp else 2.dp
    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outlineVariant

    return this
        .clip(MaterialTheme.shapes.medium)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = MaterialTheme.shapes.medium
        )
}