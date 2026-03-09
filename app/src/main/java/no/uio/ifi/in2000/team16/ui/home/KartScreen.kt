package no.uio.ifi.in2000.team16.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team16.model.Kart

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    kartViewModel: KartViewModel = hiltViewModel<KartViewModel>()
) {
    val uiState = kartViewModel.uiState.collectAsStateWithLifecycle()

    Column {
        when (uiState.value) {
            is UiState.Error -> Text("Feilet")
            is UiState.Idle -> Text("Venter")
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success -> {
                KartScreen(kart = (uiState.value as UiState.Success).data.kart)
            }
        }
    }
}

@Composable
fun KartScreen(
    kart: List<Kart>,
    modifier: Modifier = Modifier
) {
    Text(kart[0].id ?: "Ukjent")
}