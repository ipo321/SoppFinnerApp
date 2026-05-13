package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.runtime.Composable


@Composable
fun Security(
    onNavigateBack: () -> Unit,
){
    FromMenuDefaultScreen(
        header = "Personvernerklæring",
        body = "Vi samler ikke inn personopplysninger i appen vår." +
                " Når du deler posisjon og velger favorittsopper, behandles dette lokalt på din enhet.",
        onNavigateBack = onNavigateBack
    )
}

