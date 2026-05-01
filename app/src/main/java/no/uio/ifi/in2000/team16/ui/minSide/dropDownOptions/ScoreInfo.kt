package no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions

import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreenViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


@Composable
fun ScoreInfo(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: ProfileScreenViewModel = hiltViewModel()

){
    //Test to so if its loading in the terminal:
    println("ScoreInfo loaded")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {    OutlinedButton(
        onClick = onNavigateBack,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text("Tilbake")
    }
        Text(
            text = "Hvordan beregnes scoren?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(modifier=Modifier.height(24.dp))

        Text(
            text = "Det hentes ut informasjon fra en rekke api-er: " +
                    "\n\n- Metrologisk Institutt" +
                    "\n- Artsdatabasen" +
                    "\n- Kartverket" +
                    "\n- Frost" +
                    "\n- Norsk Institutt for Bioøkonomi (NIBIO)"

        )
    }
}

