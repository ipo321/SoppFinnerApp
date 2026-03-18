package no.uio.ifi.in2000.team16.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team16.domain.model.Modul

/*
    En liten default for hvordan en HomeScreen kan se ut.
 */

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    modulViewModel: ModulViewModel = hiltViewModel<ModulViewModel>()
) {
    val uiState = modulViewModel.uiState.collectAsStateWithLifecycle()

    Column {
        when (uiState.value) {
            is UiState.Error -> Text("Feilet")
            is UiState.Idle -> Text("Venter")
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success -> {
                ModulScreen(modul = (uiState.value as UiState.Success).data.modul)
            }
        }
    }
}

@Composable
fun ModulScreen(
    modul: List<Modul>,
    modifier: Modifier = Modifier
) {
    Text(modul[0].id ?: "Ukjent")
}