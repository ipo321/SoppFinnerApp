package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
    val alUiState = algorithmViewModel.uiState.collectAsState()
    val mapUiState = mapViewModel.uiState.collectAsState()
    val lat = mapUiState.value.lastClickedLat
    val lon = mapUiState.value.lastClickedLon

    FromMenuDefaultScreen(
        header = "Velg algoritme for beregning",
        body = "Her kan du velge mellom tre ulike algoritmer for å beregne score for soppfunn. " +
               "Standard-algoritmen er en lokal maskinlæringsmodell, men det er også mulig å velge " +
               "en lokal forenklet algoritme, eller en KI-evaluering.",
        onNavigateBack = onNavigateBack,
        content = { AlgorithmScreenContent(
            uiState = alUiState,
            onClick = {
                algorithmViewModel.onAlgorithmSelected(it)
                if (lat != null && lon != null) mapViewModel.onMapClicked(lat, lon)
            },
        ) }
    )
}

@Composable
fun AlgorithmScreenContent(
    uiState: State<AlgorithmUiState>,
    onClick: (String) -> Unit
) {
    val algorithms = uiState.value.availableAlgorithms
    val active = uiState.value.activeAlgorithm

    Spacer(modifier = Modifier.padding(padding.spacingSmall))
    algorithms.forEach { algorithm ->
        val isSelected = (algorithm == active)
        AlgorithmCard(
            algorithm = algorithm,
            onClick = onClick,
            isSelected = isSelected
        )
        Spacer(modifier = Modifier.padding(padding.spacingMinimum))
        Text(
            text = getAlgorithmDescription(algorithm),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.padding(padding.spacingSmall))
    }
}

@Composable
fun AlgorithmCard(
    algorithm: String,
    onClick: (String) -> Unit,
    isSelected: Boolean,
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
                .padding(padding.inner),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = algorithm,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp)
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
    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outlineVariant

    return this
        .clip(MaterialTheme.shapes.medium)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = MaterialTheme.shapes.medium
        )
}

private fun getAlgorithmDescription(algorithm: String): String =
    when (algorithm) {
        "Forenklet" -> "Den lokale forenklede algoritmen er beregnet sånn og sånn"
        "ML"    -> "Den lokale maskinlæringsmodellen bruker noe greier"
        "KI"    -> "Kunstig intelligens bruker noe greier for å beregne"
        else    -> ""
    }