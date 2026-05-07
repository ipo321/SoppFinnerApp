package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.runtime.Composable


@Composable
fun Security(
    onNavigateBack: () -> Unit,
){
    FromMenuDefaultScreen(
        header = "Personvernerklæring",
        body = "Noe tekst",
        onNavigateBack = onNavigateBack
    )
}

