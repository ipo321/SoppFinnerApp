package no.uio.ifi.in2000.team16.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team16.domain.model.ExampleClass

/*
    Default homescreen
 */

@Composable
fun ModuleScreen(
    onNavigateBack: () -> Unit,
    viewModel: ModuleViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Column {
        when (uiState.value) {
            is UiState.Error -> Text("Feilet")
            is UiState.Idle -> Text("Venter")
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success -> {
                Display(
                    onNavigateBack = onNavigateBack,
                    exampleClass = (uiState.value as UiState.Success).data.exampleClass
                )
            }
        }
    }
}

@Composable
fun Display(
    onNavigateBack: () -> Unit,
    exampleClass: List<ExampleClass>,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(exampleClass[0].id ?: "Ukjent")
        Spacer(modifier = Modifier.padding(8.dp))
        Button(
            onClick = onNavigateBack
        ) {
            Text("Kart")
        }
    }
}