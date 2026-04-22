package no.uio.ifi.in2000.team16.ui.mushroomInfo


import androidx.compose.foundation.Image
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team16.domain.model.MushroomProperty
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

/* screen for mushroom info, it has a dropdown meny button that is clickable to change mushroom,
this screen only uses resorses that are in the app and no api calls are made.
see the MushroomInfoViewModel for the dropdown meny and the Mushroom data class.

 */
@Composable
fun MushroomInfoScreen(
    onNavigateBack: () -> Unit,
    viewModel: MushroomInfoViewModel = hiltViewModel()
){
    print("MUSHROM INFOSCREEN LOADED")
    val selected = viewModel.selectedMushroom ?: return  //Return if no mushroom is selected
    val favorites by viewModel.favoriteMushrooms.collectAsState(initial = emptyList())
    val isFavorite = favorites.any { it.commonName == selected.commonName }

    var expanded by remember { mutableStateOf(false) }


    //main Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //back button

            Text(
                text = "< Utforsk",
                modifier = Modifier.fillMaxWidth().clickable { onNavigateBack() },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall

            )


        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Aldri spis sopp du er usikker på.\nSe soppkontroll.no for mer informasjon.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mushroom image in a green circle
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = selected.imageRes),
                contentDescription = selected.commonName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        //name text
        Text(
            text = selected.commonName,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = selected.latinName,
            style = MaterialTheme.typography.labelMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        //favorite toggel icon
        /*Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { viewModel.toggleFavorite(selected) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavorite) "Fjern fra favoritter" else "Legg til favoritter",
                    tint = if (isFavorite) Color(0xFFD94F4F) else Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
            Log.d("T16_Mushroom_InfoScreen", "Toggled favorite for ${selected.commonName}" + "  favorites: ${isFavorite}")
        }*/

        Spacer(modifier = Modifier.height(16.dp))
        //info badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            selected.properties.forEach { property ->
                MushroomPropertyBadge(property)
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        //favorite button

        OutlinedButton(
            onClick = { viewModel.toggleFavorite(selected) },
            modifier = Modifier.wrapContentSize()
        ) {
            Icon(imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Fjern fra favoritter" else "Legg til i favoritter")
            Spacer(Modifier.width(8.dp))
            Text(if (isFavorite) "Fjern fra favoritter" else "Legg til i favoritter")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Om soppen",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = selected.bioRes.toInt()),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
@Composable
fun MushroomPropertyBadge(property: MushroomProperty) {
    val borderColor = if (property.isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
    val bgColor     = if (property.isNegative) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(1.5.dp, borderColor.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = property.iconRes),
                contentDescription = property.label,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = property.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}