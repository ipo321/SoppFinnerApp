package no.uio.ifi.in2000.team16.ui.mushroomInfo

import no.uio.ifi.in2000.team16.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team16.data.species.SpeciesDataSource

/* screen for mushroom info, it has a dropdown meny button that is clickable to change mushroom,
this screen only uses resorses that are in the app and no api calls are made.
see the MushroomInfoViewModel for the dropdown meny and the Mushroom data class.

 */
@Composable
fun MushroomInfoScreen(
    onNavigateHome: () -> Unit,
    viewModel: MushroomInfoViewModel = viewModel()) {
    val selected = viewModel.selectedMushroom
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD4E6CC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Aldri spis sopp du er usikker på." +
                " se soppkontroll.no for mere informasjon",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold)

        Box {
        Button(onClick = { expanded = true }) {
            Text(selected.commonName)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            viewModel.mushrooms.forEach { mushroom ->
                DropdownMenuItem(
                    text = { Text(mushroom.commonName) },
                    onClick = {
                        viewModel.selectMushroom(mushroom)
                        expanded = false
                    }
                )
            }
        }
    }
        // Mushroom image in a green circle
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color(0xFF8FAF82)),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = selected.imageRes),
                contentDescription = selected.commonName,
                modifier = Modifier.size(160.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = selected.commonName, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = selected.latinName, fontSize = 14.sp, fontStyle = FontStyle.Italic)

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            selected.properties.forEach { property ->
                MushroomPropertyBadge(property)
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNavigateHome,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Tilbake til kart")
        }
    }
}
@Composable
fun MushroomPropertyBadge(property: SpeciesDataSource.MushroomProperty) {
    val borderColor = if (property.isNegative) Color(0xFFD94F4F) else Color(0xFF7A9B6E)
    val bgColor     = if (property.isNegative) Color(0xFFFDE8E8) else Color(0xFFE8F0E2)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(2.5.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = property.iconRes),
                contentDescription = property.label,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = property.label, fontSize = 12.sp)
    }
}