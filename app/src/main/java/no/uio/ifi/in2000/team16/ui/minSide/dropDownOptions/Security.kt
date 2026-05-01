package no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions

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
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreenViewModel


@Composable
fun Security(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: ProfileScreenViewModel = hiltViewModel()

){
    //Test to so if its loading in the terminal:
    println("Security loaded")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
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
            text = "Personvernerklæring",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(modifier=Modifier.height(24.dp))

        Text(
            text = "Hensikten med appen er å kunne hjelpe sopp plukkere med å vurdere hvor og når den beste tiden " +
                    "er å plukke sopp. Scoren man får er basert på vår vurdering av sesong, værforhold og hva vi vet om vekstbetingelser" +
                    "til de ulike soppene. " +
                    "\n Soppsiden gir en oversikt over ulike sopper,om der er i sesong, giftig" +
                    "og om de er en god matsopp. I tillegg er det litt informasjon om soppen. Soppsiden er kun ment for å veilede brukere" +
                    "og skal ikke brukes som grunnlag for å identifisere sopp eller avgjøre om det er en trygg sopp å spise." +
                    "\n Det er brukerens eget ansvar å identifisere sopp, vi anbefaler å bruke tjenester som Soppkontroll.no. " +
                    "Vi har prøvd vårt beste å sikre at informasjonen i appen er korrrekt, men kan ikka garantere at den er feilfri. " +
                    "Identifikasjon av sopp skjer på eget ansvar, og utviklerne fraskriver seg ansvar for eventuelle skader som følge av informasjonen fra soppsiden."
        )

    }
}

