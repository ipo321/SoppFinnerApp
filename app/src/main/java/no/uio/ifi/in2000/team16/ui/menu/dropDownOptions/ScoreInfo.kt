package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import no.uio.ifi.in2000.team16.ui.menu.MenuViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team16.ui.common.BackButton


@Composable
fun ScoreInfo(
    onNavigateBack: () -> Unit,
){
    //Test to so if it's loading in the terminal:
    println("ScoreInfo loaded")
    FromMenuDefaultScreen(
        header = "Hvordan beregnes scoren?",
        body = "Det hentes ut informasjon fra en rekke api-er: " +
                "\n\n- Metrologisk Institutt" +
                "\n- Artsdatabasen" +
                "\n- Kartverket" +
                "\n- Frost" +
                "\n- Norsk Institutt for Bioøkonomi (NIBIO)",
        onNavigateBack = onNavigateBack
    )
}

