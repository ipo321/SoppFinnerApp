package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.runtime.Composable

@Composable
fun InfoScreen(
    onNavigateBack: () -> Unit,
){
    //Test to so if it's loading in the terminal:
    println("InfoScreen loaded")
    FromMenuDefaultScreen(
        header = "Om appen",
        body = "Denne appen blablabla",
        onNavigateBack = onNavigateBack
    )
}

