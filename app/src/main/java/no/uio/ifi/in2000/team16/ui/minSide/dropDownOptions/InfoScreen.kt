package no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreenViewModel


@Composable
fun InfoScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: ProfileScreenViewModel = androidx.hilt.navigation.compose.hiltViewModel()

){
    //Test to so if its loading in the terminal:
    println("InfoScreen loaded")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {    OutlinedButton(
        onClick = onNavigateBack,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text("Tilbake")
    }
        Text(
            text = "Om appen",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(modifier=Modifier.height(24.dp))
        Text(
            text = "Appen tilbyr et enkelt kart, hvor brukerne kan velge en sopp, trykke på et punkt og få en evaluering av soppsanke-potensialet i form av en score fra 0-100, " +
                    "og en oversikt over variablene brukt til å generere den. Kartet tilbyr også et heat map som viser potensialet for et større område, samt mulighet til å se tidligere sopp-funn fra Artsdatabanken. " +
                    "For å evaluere et område samler appen inn informasjon om historisk vær (MET-Frost), nåværende vær (MET-LocationForecast), Mushroom findings (GBIF), biologisk data (NIBIO SAT-SKOG) og ressurstype-data (NIBIO AR5). Alt innsamlet data fra de ulike API-ene brukes deretter til en lokal maskinlæringsmodell for å kalkulere en score.Alternativt tilbyr appen KI-evaluering og en forenklet lokal algoritme. \n" +
                    "\n" +
                    "Soppsiden gir deg mulighet til å velge hvilken sopp du ønsker å bruke i kartet, samt enkel informasjon om hver sopp i appen. " +
                    "Soppene du legger til som favoritt, kan velges som hurtigvalg i dropdown-menyen i kartet.\n",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )

    }
}

