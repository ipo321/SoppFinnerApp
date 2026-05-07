package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.runtime.Composable

@Composable
fun ScoreInfo(
    onNavigateBack: () -> Unit,
){
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

