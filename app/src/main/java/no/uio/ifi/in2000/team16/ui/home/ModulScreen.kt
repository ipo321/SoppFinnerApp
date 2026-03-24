package no.uio.ifi.in2000.team16.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.logic.mushroom.MushroomScorer
import no.uio.ifi.in2000.team16.domain.logic.mushroom.MushroomScorerV1
import no.uio.ifi.in2000.team16.domain.logic.mushroom.MushroomScorerV2
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile

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
    val scorer: MushroomScorer = remember { MushroomScorerV2() }
    val speciesRepo = remember { SpeciesRepository() }
    val skarlagenProfile = remember {
        speciesRepo.getSpeciesByName("Skarlagen vårbeger") ?: SpeciesProfile.mock
    }

    //val evaluation = scorer.calculateScore(context, SpeciesProfile.mock)
    val evaluation = scorer.calculateScore(context, skarlagenProfile)

    Column(horizontalAlignment = Alignment.Start) {

        // --- NYTT: Viser algoritme-scoren ---
        Text("Algoritme Resultat", fontWeight = FontWeight.Bold)
        Text("Score: ${evaluation.score} / 100")
        Text("Begrunnelse: ${evaluation.description}")

        Spacer(modifier = Modifier.height(16.dp))

        // --- DIN ORIGINALE KODE UNDER HER ---
        Text("Generell Info", fontWeight = FontWeight.Bold)
        Text("Aktiv Måned: ${context.currentMonth}")
        Text("Historiske funn her: ${context.historicalFinds}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Værdata (Yr)", fontWeight = FontWeight.Bold)
        context.currentWeather?.let { weather ->
            Text("Nåværende temp: ${weather.temp} °C")
            Text("Symbol: ${weather.symbolCode}")
        } ?: Text("Klarte ikke hente Yr-data")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Historisk vær (Frost)", fontWeight = FontWeight.Bold)
        context.weatherHistory?.let { history ->
            Text("Gjennomsnittstemp: ${history.avgTempPast14Days?.let { "%.2f".format(it) } ?: "N/A"} °C")
            Text("Total nedbør: ${history.totalRainPast14Days?.let { "%.1f".format(it) } ?: "N/A"} mm")
        } ?: Text("Ingen historikk funnet", color = androidx.compose.ui.graphics.Color.Red)

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



