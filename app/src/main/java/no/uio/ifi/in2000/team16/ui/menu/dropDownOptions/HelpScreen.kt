package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.runtime.Composable

@Composable
fun HelpScreen(
    onNavigateBack: () -> Unit,
) {
    FromMenuDefaultScreen(
        header = "Informasjon",
        body = "lalala",
        onNavigateBack = onNavigateBack
    )
}
