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
        body = "Dette er en app for sopp-plukking i Norge. \n" +
                "\n" +
                "Kartet tilbyr et enkelt kart, hvor brukerne kan velge en sopp, trykke på et punkt og få en evaluering av soppsanke-potensialet i form av en score fra 0-100, og en oversikt over variablene brukt til å generere den. Kartet tilbyr også et heat map som viser potensialet for et større område, samt mulighet til å se tidligere sopp-funn fra Artsdatabanken. For å evaluere et område samler appen inn informasjon om historisk vær (MET-Frost), nåværende vær (MET-LocationForecast), Mushroom findings (GBIF), biologisk data (NIBIO SAT-SKOG) og ressurstype-data (NIBIO AR5). Alt innsamlet data fra de ulike API-ene brukes deretter til en lokal maskinlæringsmodell for å kalkulere en score.Alternativt tilbyr appen KI-evaluering og en forenklet lokal algoritme. \n" +
                "\n" +
                "Soppsiden gir deg mulighet til å velge hvilken sopp du ønsker å bruke i kartet, samt enkel informasjon om hver sopp i appen. Soppene du legger til som favoritt, kan velges som hurtigvalg i dropdown-menyen i kartet.\n",
        onNavigateBack = onNavigateBack
    )
}

