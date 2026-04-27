package no.uio.ifi.in2000.team16.ui.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FlashlightOff
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme

/*
Dropdown Menu for the map

 */

@Composable
fun Dropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onNavigateToDev: () -> Unit = {},
    onToggleHeatMap: () -> Unit = {},
    isHeatmapEnabled: Boolean = false,
    isSatelliteMode: Boolean = false,
    onToggleSatellite: () -> Unit = {},
    showWmsForest: Boolean = false,
    onToggleWmsForest: () -> Unit = {},
    favoriteMushrooms: List<Mushroom> = emptyList(),
    selectedMushroom: Mushroom? = null,
    onMushroomSelected: (Mushroom) -> Unit = {},
    isHistoricalFindsEnabled: Boolean,
    onToggleHistoricalFinds: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(x = 0.dp, y = 10.dp)
    ) {


        // First section
        DropdownMenuItem(
            text = { Text(if (isHeatmapEnabled) "Skru av Heatmap" else "Skru på Heatmap") },
            leadingIcon = {
                if (isHeatmapEnabled) {
                    Icon(Icons.Outlined.FlashlightOff, contentDescription = "Flashlight Off")
                } else {
                    Icon(Icons.Outlined.FlashlightOn, contentDescription = "Flashlight On")
                }
            },
            onClick = {
                onToggleHeatMap()
                onDismissRequest()
            }
        )
        if (isHeatmapEnabled) {
            ColorRow(
                numberOfRows = 3,
                colors = listOf( // Colors taken from heatmap
                    Color(0xFFF4BFBE),
                    Color(0xFFF5E399),
                    Color(0xFFADDB94)
                ),
                text = listOf("Lavere score", "Middels score", "Høyere score"),
            )
        }

        DropdownMenuItem(
            text = { Text(if (isHistoricalFindsEnabled) "Skru av historiske funn" else "Skru på historiske funn") },
            leadingIcon = { Icon(Icons.Outlined.Book, contentDescription = null) },
            onClick = {
                onToggleHistoricalFinds()
                onDismissRequest()
            }
        )

        DropdownMenuItem(
            text = { Text(if (isSatelliteMode) "Kartmodus" else "Satellittmodus") },
            leadingIcon = { Icon(Icons.Outlined.Layers, contentDescription = "Kartlag-ikon") },
            onClick = { onToggleSatellite() }
        )

        DropdownMenuItem(
            text = { Text(if (showWmsForest) "Skjul skoglag" else "Vis skoglag") },
            leadingIcon = { Icon(Icons.Outlined.Forest, contentDescription = "Skog-ikon") },
            onClick = { onToggleWmsForest() }
        )

        DropdownMenuItem(
            text = { Text("Dev") },
            leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
            onClick = {
                onNavigateToDev()
                onDismissRequest()
            }
        )

        HorizontalDivider()

        if (favoriteMushrooms.isNotEmpty()) {
            //HorizontalDivider()
            Text(
                text = "Velg sopp som vises på kartet:",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            favoriteMushrooms.forEach { mushroom ->
                val isSelected = mushroom.commonName == selectedMushroom?.commonName
                DropdownMenuItem(
                    text = {
                        Text(
                            text = mushroom.commonName,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onMushroomSelected(mushroom)
                        onDismissRequest()
                    },
                    trailingIcon = {
                        if (isSelected) Icon(
                            Icons.Outlined.Check,
                            contentDescription = "Check"
                        )
                    }
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun DropdownPreview() {
    Dropdown(
        expanded = true,
        onDismissRequest = {},
        onNavigateToDev = {},
        isHistoricalFindsEnabled = false,
        isHeatmapEnabled = false,
        onToggleHistoricalFinds = {})
}




