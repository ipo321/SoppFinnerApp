package no.uio.ifi.in2000.team16.ui.mushroomInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme.padding

/*
the list screen for the mushroom info side.
hold each mushroom in a list, and routs to the mushroom info screen when cliked.
 */
@Composable
fun MushroomListScreen(
    navigateToMushroom: () -> Unit,
    onMushroomControl: () -> Unit,
    viewModel: MushroomInfoViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    val selectedMushroom = uiState.selectedMushroom
    //main Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ){
        //topText
        Text(
            text = "Soppsiden",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(padding.outer))
        //list column with all the mushrooms.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(padding.inner)
        ) {
            //Spacer(modifier = Modifier.height(8.dp))
            WarningBanner()
            //Spacer(modifier = Modifier.height(8.dp))
            MushroomControlBanner(onVisitSoppkontroll = onMushroomControl)
            //Spacer(modifier = Modifier.height(4.dp))
            //favorites column
            Text(
                text = "Favoritter",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            uiState.favorites.forEach { mushroom ->
                MushroomListItem(
                    mushroom = mushroom,
                    isSelected = selectedMushroom?.commonName == mushroom.commonName,
                    onClick = {
                        viewModel.onMushroomSelected(mushroom)
                    },
                    navigateToMushroom = {
                        viewModel.onMushroomViewing(mushroom)  // set which mushroom to display
                        navigateToMushroom()
                    }
                )
            }
            Text(
                text = "Øvrige",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            uiState.otherMushrooms.forEach { mushroom ->
                MushroomListItem(
                    mushroom = mushroom,
                    isSelected = selectedMushroom?.commonName == mushroom.commonName,
                    onClick = {
                        viewModel.onMushroomSelected(mushroom)
                    },
                    navigateToMushroom = {
                        viewModel.onMushroomViewing(mushroom)  // set which mushroom to display
                        navigateToMushroom()
                    }
                )
            }

        }

    }
}

//the itemst that will be put in the list
//Click on the item itself will take you to that mushroom
@Composable
fun MushroomListItem(
    mushroom: Mushroom,
    isSelected: Boolean,
    onClick: () -> Unit,
    navigateToMushroom: () -> Unit,
) {
    //val cardModifier = mushroomCardModifier(isSelected)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .mushroomCard(isSelected)
            .clickable {
                navigateToMushroom() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = mushroom.imageRes),
            contentDescription = mushroom.commonName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mushroom.commonName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = mushroom.latinName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        //box for choosing to select this mushroom for the search
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onClick() },
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}
//extention Function for the mushroom card. makes it highlighted if it is chosen.
@Composable
private fun Modifier.mushroomCard(isSelected: Boolean): Modifier {
    val borderWidth = if (isSelected) 4.dp else 2.dp
    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.outlineVariant

    return this
        .clip(MaterialTheme.shapes.medium)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = MaterialTheme.shapes.medium
        )
}


@Composable
fun MushroomControlBanner(onVisitSoppkontroll: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth()
            .border(width = 4.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Logo + text row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.soppkontroll_logo),
                    contentDescription = "Soppkontroll logo",
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Soppkontroll.no",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "tilbyr digital soppkontroll som lar brukerne sjekke om soppen er spiselig eller ikke.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Orange button
            Button(
                onClick = onVisitSoppkontroll,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07B20)),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Besøk Soppkontroll.no", color = Color.White)
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun WarningBanner(){
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
            .border(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.errorContainer, width = 1.dp),

    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )


            Text(
                text = "OBS: Dette er ikke en app for identifikasjon av sopp",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

