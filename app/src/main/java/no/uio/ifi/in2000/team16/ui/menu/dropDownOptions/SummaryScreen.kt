package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import no.uio.ifi.in2000.team16.R
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SummaryScreen(
    navController: NavController,
    onNavigateBack: () -> Unit

){
    FromMenuDefaultScreen(
        header = "Guide",
        body = "",
        onNavigateBack = onNavigateBack,
        content = {HelpScreen(
            navController = navController,
            onNavigateBack = onNavigateBack
        )}

    )
}


data class HelpSection(
    val title: String,
    val text: String,
    val imageRes: Int
)
val helpSections = listOf(
    HelpSection(
        title= "Velkommen",
        text="Sopp sporer er ett verktøy for å beregne om ulike områder har potensialet for sopp på en skala fra 1-100",
        imageRes = R.drawable.cute_mushroom
    ),
        HelpSection(
            title = "Kart",
            text = "Når du først åpner appen vil du se kartet. Der kan du trykke på en" +
                    " plass i karetet eller søke det opp og den vil automatisk beregne skoren",
            imageRes = R.drawable.map_screen
        ),
        HelpSection(
            title = "Heat map",
            text = "Oppe til venstre er det en liten meny. Inni den kan du skru på heatmap" +
                    "Den vil markere hele kartet i rødt, grønt eller gult basert på om områdene har " +
                    "mye eller lite potensiale for sopp. Rødt betyr lite, grønt er mye og gult er litt ",
            imageRes = R.drawable.heatmap
        ),
        HelpSection(
            title = "Historiske funn",
            text = "Under heatmap kan man skru på historiske funn. Denne lar deg se hvor det tidligere har blitt funnet sopp." +
                    "Denne informasjonen ble tatt fra artsdatabanken",
            imageRes = R.drawable.historical
        ),
        HelpSection(
            title = "Satelittmodus",
            text = "Her kan du enkelt endre kartmodus mellom sattelitt og standard kartmodus",
            imageRes = R.drawable.mapmode
        ),
        HelpSection(
            title = "Skoglag",
            text = "Her kan du velge om du vil se på skoglagene i kartet eller ikke, fargene symboliserer hvilke tretype som dominerer området",
            imageRes = R.drawable.forest
        ),
        HelpSection(
            title = "Score",
            text = "Når du trykker på et punkt i kartet vil du då en evaluering av soppsanke-potensialer i en form av en" +
                    "score fra 0-100. Når du første starter appen vil den automatisk være satt på kantarell, dette kan endres" +
                    "Ved å utvide scorekortet kan du se mer detaljert informasjon om området, som temperatur, nedbør, vind, tresorter," +
                    " bonitet og andre faktorer som påvirker potensialet for sopp." ,
            imageRes = R.drawable.score
        ),
        HelpSection(
            title = "Soppsiden",
            text = "Soppsiden er en generell oversikt over de ulike soppene du kan finne, om de er giftige og om de er en god matsopp." +
                    " Her finner du også en link til soppkontroll.no med en mer detaljert oversikt og mulighet til soppkontroll når det er soppsesong.",
            imageRes = R.drawable.soppsiden,
        ),
        HelpSection(
            title = "Sopp profil",
            text = "Inn i hver sopp profil kan du se hvis den er i sesong, giftig eller en god matsopp. " +
                    "Du kan også legge den til i favoritter for raskere tilgang eller velge den i kart og bli sendt direkte til kartsiden.",
            imageRes = R.drawable.profile,
        ),
        HelpSection(
            title = "Mørkmodus",
            text = "I menysiden kan du finne mørk modus knappen for å raskt bytte mellom lys modus og mørk modus",
            imageRes = R.drawable.darkmode,
        ),
        HelpSection(
            title = "Giftinformasjon",
            text = "Ved mistanke om soppforgiftning kan du kontakte giftinformasjonen fra meny siden.",
            imageRes = R.drawable.poison,
        ),
        HelpSection(
            title = "Beregningsalgoritme",
            text = "Under beregningsalgoritmer kan du selv velge hvordan appen skal beregne score i kartet. " +
                    "Alle algoritmene evaluerer soppsanke-potensialet, men på ulike måter." +
                    "\n ML: En algoritme som er trent på tidligere soppfunn og værdata i Norge. " +
                    "\n Forenklet modell: Bruker en enklere modell basert på kjente forhold som temperatur, fuktighet og skogstype." +
                    "\n KI: Bruker en språkmodell til å vurdere forholdene som er programmert til å være din egne sopp spesialist. ",
            imageRes = R.drawable.algorithm,
        ),
        HelpSection(
            title = "Guide til appen",
            text = "Ved usikkerhet eller behov for hjelp kan du åpne guide-siden. Her finner du forklaringer på de viktigste funksjonene i appen, hvordan de brukes og hva de ulike knappene betyr.",
            imageRes = R.drawable.guide,
        ),
    )
@Composable
fun HelpScreen(
    navController: NavController,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        helpSections.forEach { section ->
            HelpSectionItem(section)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HelpSectionItem(section: HelpSection) {
    var showImageDialog by remember { mutableStateOf(false) }

    if (showImageDialog) {
        ImageDialog(
            imageRes = section.imageRes,
            title = section.title,
            onDismiss = { showImageDialog = false }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Image(
                painter = painterResource(id = section.imageRes),
                contentDescription = section.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { showImageDialog = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Trykk på bildet for å forstørre",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = section.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = section.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            
        )
    }
}
@Composable
fun ImageDialog(
    imageRes: Int,
    title: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Lukk")
                }
            }
        },
        title = {
            Text(title)
        },
        text = {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            )
        }
    )
}