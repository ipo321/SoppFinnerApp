package no.uio.ifi.in2000.team16.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext

@Composable
fun ModuleScreen(
    onNavigateBack: () -> Unit,
    viewModel: ModuleViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = uiState.value) {
            is UiState.Error -> Text("Feilet! Sjekk Logcat for detaljer.")
            is UiState.Idle -> Text("Trykk på knappen for å hente alt!")
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success -> {
                DisplayEnvironmentContext(state.data.environmentContext)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { viewModel.testEnvironmentUseCase() }) {
            Text("Hent Vær & Skog (UseCase)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onNavigateBack) {
            Text("Tilbake til Kart")
        }
    }
}

@Composable
fun DisplayEnvironmentContext(context: EnvironmentContext) {
    Column(horizontalAlignment = Alignment.Start) {
        Text("Generell Info", fontWeight = FontWeight.Bold)
        Text("Aktiv Måned: ${context.currentMonth}")
        Text("Historiske funn her: ${context.historicalFinds}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Værdata (Yr)", fontWeight = FontWeight.Bold)
        // Her forutsetter jeg at currentWeather har disse feltene, juster hvis din modell er ulik!
        val weather = context.currentWeather
        if (weather != null) {
            Text("Detaljer lastet fra Yr: Ja (Se objektet i Logcat for nøyaktig data)")
        } else {
            Text("Klarte ikke hente værdata")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Skogdata (NIBIO)", fontWeight = FontWeight.Bold)
        val forest = context.forestData
        if (forest != null) {
            Text("Hovedtreslag: ${forest.dominantTreeSpecies ?: "Ukjent"}")
            Text("Gran: ${forest.sprucePercentage}% | Furu: ${forest.pinePercentage}% | Lauv: ${forest.broadleafPercentage}%")
            Text("Jordkvalitet (Bonitet): ${forest.fertilityCategory ?: "Ukjent"} (${forest.siteIndex})")
            Text("Skogens alder: ${forest.standAge} år")
        } else {
            Text("Klarte ikke hente skogdata")
        }
    }
}