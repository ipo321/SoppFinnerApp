package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

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
import no.uio.ifi.in2000.team16.ui.menu.MenuViewModel


@Composable
fun Security(
    onNavigateBack: () -> Unit,
){
    //Test to so if it's loading in the terminal:
    println("Security loaded")
    FromMenuDefaultScreen(
        header = "Personvernerklæring",
        body = "Noe tekst",
        onNavigateBack = onNavigateBack
    )
}

