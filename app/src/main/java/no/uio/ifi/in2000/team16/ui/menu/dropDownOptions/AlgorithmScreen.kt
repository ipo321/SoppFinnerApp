package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
                "Alle de ulike metodene sjekker opp forholdene der du velger for å beregne hvor godt " +
                "egnet et område er for å plukke sopp! Standard-algoritmen er den lokale maskinlæringsmodellen, " +
                "men det er også mulig å velge en forenklet algoritme, eller en KI-evaluering.",
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
            // RadioButton selects this algorithm for the calculation
            RadioButton(
                selected = isSelected,
                onClick = { onClick(algorithm) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.outline
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
        "Forenklet" -> "Bruker er algoritme bygget på biologisk forståelse av sopper. Hver sopp har også med maskinlæring fått bygget en \"ønskeliste\" over hvilke forhold den foretrekker, som sammen med algoritmen gir deg en score."
        "ML"    -> "Bruker en algoritme som er lært opp på tusen tidligere soppfunn i Norge for å gjenkjenne mønstre i vær og skog."
        "KI"    -> "Bruker en moderne, stor språkmodell for å vurdere forholdene. Den har fått beskjed om å være din egne sopp spesialist."
        else    -> ""
    }