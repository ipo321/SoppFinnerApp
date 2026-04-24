package no.uio.ifi.in2000.team16.ui.mushroomInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import no.uio.ifi.in2000.team16.domain.model.Mushroom

/*
the list screen for the mushroom info side.
hold each mushroom in a list, and routs to the mushroom info screen when cliked.
 */
@Composable
fun MushroomListScreen(
    navigateToMushroom: () -> Unit,
    viewModel: MushroomInfoViewModel = hiltViewModel()
){
    val mushrooms = viewModel.mushrooms
    val favorites by viewModel.favoriteMushrooms.collectAsState(initial = emptyList())
    val selectedMushroom = viewModel.selectedMushroom
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
        Spacer(modifier = Modifier.height(16.dp))

        //list column with all the mushrooms.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
            text = "Favoritter",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )
            mushrooms.filter { favorites.any { fav -> fav.commonName == it.commonName } }.forEach{ mushroom ->
                MushroomListItem(
                    viewModel = viewModel,
                    mushroom = mushroom,
                    isFavorite = favorites.any { it.commonName == mushroom.commonName },
                    isSelected = selectedMushroom?.commonName == mushroom.commonName,
                    onClick = {
                        viewModel.onMushroomSelected(mushroom)
                    },
                    navigateToMushroom = navigateToMushroom
                )
            }
            Text(
                text = "Øvrige",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            mushrooms.filterNot { favorites.any { fav -> fav.commonName == it.commonName } }.forEach { mushroom ->
                MushroomListItem(
                    viewModel = viewModel,
                    mushroom = mushroom,
                    isFavorite = favorites.any { it.commonName == mushroom.commonName },
                    isSelected = selectedMushroom?.commonName == mushroom.commonName,
                    onClick = {
                        viewModel.onMushroomSelected(mushroom)
                    },
                    navigateToMushroom = navigateToMushroom
                )
            }

        }

    }
}

//the itemst that will be put in the list
@Composable
fun MushroomListItem(
    viewModel: MushroomInfoViewModel,
    mushroom: Mushroom,
    isFavorite: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    navigateToMushroom: () -> Unit,
) {
    val cardModifier = mushroomCardModifier(isSelected)


    Row(
        modifier = cardModifier
            .clickable { onClick() }
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
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .padding(end = 16.dp)
                .size(28.dp)
                .border(
                    width = 2.dp,
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                .clickable {
                    viewModel.onMushroomViewing(mushroom)
                    navigateToMushroom()},
            contentAlignment = Alignment.Center
        ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "go to mushroom",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

@Composable
private fun mushroomCardModifier(
    isSelected: Boolean
): Modifier {
    val base = Modifier.fillMaxWidth()

    return if (isSelected) {
        base
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 4.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            )

    } else {
        base
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium
            )

    }
}

