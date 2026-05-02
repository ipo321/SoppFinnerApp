package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.runtime.Composable

@Composable
fun PoisonControl(
    onNavigateBack: () -> Unit,
){
    //Test to so if it's loading in the terminal:
    println("PoisonScreen loaded")
    FromMenuDefaultScreen(
        header = "Giftinformasjonen",
        body = "Dersom du mistenker soppforgiftning bør du ta kontakt med Giftinformasjonen umiddelbart. De kan nås hele døgnet.",
        bodyCentered = "TLF: 22 59 13 00",
        onNavigateBack = onNavigateBack
    )

}

