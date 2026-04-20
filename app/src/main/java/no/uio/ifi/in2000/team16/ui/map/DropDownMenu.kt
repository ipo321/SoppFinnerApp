package no.uio.ifi.in2000.team16.ui.map


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import no.uio.ifi.in2000.team16.domain.model.Mushroom

@Composable
fun Dropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onNavigateToDev: () -> Unit = {},
    onToggleHeatMap: () -> Unit = {},
    isHeatmapEnabled: Boolean = false,
    favoriteMushrooms: List<Mushroom> = emptyList(),
    selectedMushroom: Mushroom? = null,
    onMushroomSelected: (Mushroom) -> Unit = {},
    isHistoricalFindsEnabled: Boolean,
    onToggleHistoricalFinds: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset= DpOffset(x=(-20).dp,y=0.dp)
    ) {


        // First section
        DropdownMenuItem(
            text = { Text(if (isHeatmapEnabled) "Skru av Heatmap" else "Skru på Heatmap") },
            leadingIcon = { Icon(Icons.Outlined.Create, contentDescription = null) },
            onClick = {
                onToggleHeatMap()
                onDismissRequest()
            }
        )

        DropdownMenuItem(
            text = { Text(if (isHistoricalFindsEnabled) "Skru av historiske funn" else "Skru på historiske funn") },
            leadingIcon = { Icon(Icons.Outlined.Create, contentDescription = null) },
            onClick = {
                onToggleHistoricalFinds()
                onDismissRequest()
            }
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
            HorizontalDivider()
            Text(
                text = "Hurtigvalg:",
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
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DropdownPreview() {
    Dropdown(
        expanded = true,
        onDismissRequest = {},
        onNavigateToDev = {},
        isHistoricalFindsEnabled = false,
        onToggleHistoricalFinds = {})
}




