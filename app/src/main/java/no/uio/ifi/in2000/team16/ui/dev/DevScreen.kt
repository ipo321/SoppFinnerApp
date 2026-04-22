package no.uio.ifi.in2000.team16.ui.dev

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import no.uio.ifi.in2000.team16.domain.model.*
import no.uio.ifi.in2000.team16.ui.map.MapViewModel

@Composable
fun DevScreen(
    mapViewModel: MapViewModel,
    onNavigateBack: () -> Unit,
    devViewModel: DevViewModel = hiltViewModel()
) {
    val mapState by mapViewModel.uiState.collectAsState()
    val devState by devViewModel.uiState.collectAsState()
    val context = mapState.currentContext

    Column(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Button(onClick = onNavigateBack) {
            Text("← Tilbake til kart")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Kartinnstillinger", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Satellittmodus")
            Switch(
                checked = mapState.isSatelliteMode,
                onCheckedChange = { mapViewModel.toggleSatellite() }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Vis NIBIO Skoglag (WMS)")
            Switch(
                checked = mapState.showWmsForest,
                onCheckedChange = { mapViewModel.toggleWmsForest() }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Spacer(modifier = Modifier.height(16.dp))
        Text("Utvikler Konsoll", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Algoritme", fontWeight = FontWeight.Bold)
                AlgorithmDropdown(devState) { devViewModel.onAlgorithmSelected(it) }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Art", fontWeight = FontWeight.Bold)
                MushroomDropdown(devState) { devViewModel.onMushroomSelected(it) }
            }
        }
        Button(
            onClick = { devViewModel.recalculate(context) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = context != null
        ) {
            Text("Rekalkuler poengsum")
        }

        // Resultatvisning
        devState.lastEvaluation?.let { eval ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resultat: ${eval.score}/100", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Forklaring: ${eval.description}")
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text("Siste lagrede kontekst", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        if (context == null) {
            Text("Ingen data. Trykk på kartet først.", fontStyle = FontStyle.Italic, color = Color.Gray)
        } else {
            ContextGridSection(context)
        }
    }
}

@Composable
fun ContextGridSection(context: EnvironmentContext) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        SectionHeader("Vær & Historikk")
        context.currentWeather?.let { w ->
            GridRow("Temp", "${w.temp ?: "?"}°C", "Regn", "${w.rain ?: "?"}mm")
            GridRow("Rain%", "${w.probOfRain ?: "?"}%", "Tåke", "${w.fogFraction ?: "?"}m/s")
            GridRow("Fukt", "${w.humidity ?: "?"}%", "Vind", "${w.windSpeed ?: "?"}m/s")
        }
        context.weatherHistory?.let { h ->
            GridRow("Avg 14d", "${h.avgTempPast14Days ?: "?"}°C", "Regn 14d", "${h.totalRainPast14Days ?: "?"}mm")
            GridRow("min 14d", "${h.minTempPast14Days ?: "?"}°C", "maks 14d", "${h.maxTempPast14Days ?: "?"}mm")
        }

        SectionHeader("Skog & Terreng")
        context.forestData?.let { f ->
            GridRow("Bestand", "${f.standAge ?: "?"} år", "Høyde", "${f.maxElevation ?: "?"}m")
            GridRow("Gran", "${f.sprucePercentage ?: "?"}%", "Furu", "${f.pinePercentage ?: "?"}%")
            GridRow("Lauv", "${f.broadleafPercentage ?: "?"}%", "Bonitet", f.fertilityCategory ?: "-")
        }
        context.terrainData?.let { t ->
            GridRow("Helning", "${t.slope}°", "Retning", "${t.direction}°")
        }

        SectionHeader("Lokasjonsparametere")
        GridRow("AR5", context.ar5 ?: "N/A", "Måned", context.currentMonth.toString())
        GridRow("Funn", "${context.historicalFindCount} stk", "", "")
    }
}

@Composable
fun GridRow(label1: String, value1: String, label2: String, value2: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        InfoCell(label1, value1, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        InfoCell(label2, value2, modifier = Modifier.weight(1f))
    }
}

@Composable
fun InfoCell(label: String, value: String, modifier: Modifier) {
    if (label.isEmpty()) return Spacer(modifier)
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AlgorithmDropdown(state: DevUiState, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(" ${state.activeAlgorithm}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            state.availableAlgorithms.forEach { algo ->
                DropdownMenuItem(
                    text = { Text(algo) },
                    onClick = {
                        onSelected(algo)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MushroomDropdown(state: DevUiState, onSelected: (Mushroom) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Sopp: ${state.selectedMushroom?.commonName ?: "Ingen"}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            state.availableMushrooms.forEach { mushroom ->
                DropdownMenuItem(
                    text = { Text(mushroom.commonName) },
                    onClick = {
                        onSelected(mushroom)
                        expanded = false
                    }
                )
            }
        }
    }
}