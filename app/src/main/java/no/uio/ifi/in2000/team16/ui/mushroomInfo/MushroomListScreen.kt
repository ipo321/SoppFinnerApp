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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.ui.common.WarningBanner
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme.padding


/**
 * The main list screen for Soppesiden.
 *
 * Shows all mushrooms split into two sections: favorites and others.
 * Tapping a row navigates to that mushroom's detail screen (MushroomInfoScreen).
 * The checkbox selects which mushroom is shown on the map.
 */
@Composable
fun MushroomListScreen(
    navigateToMushroom: () -> Unit,
    onMushroomControl: () -> Unit,
    viewModel: MushroomInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedMushroom = uiState.selectedMushroom
    //main Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(start = padding.outerMedium, padding.outerMedium, padding.outerMedium)
    ) {
        //topText
        Text(
            text = stringResource(R.string.mushroom_list_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Spacer(modifier = Modifier.height(padding.outer))
        // Scrollable column containing banners and both mushroom sections.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(padding.inner)
        ) {

            WarningBanner(
                text = stringResource(R.string.warning_not_for_identification),
                textStyle = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(padding.inner))

            MushroomControlBanner(onVisitSoppkontroll = onMushroomControl)

            //favorites column
            MushroomSelectionSection(
                title = stringResource(R.string.mushroom_list_favorites),
                isFavorites = true,
                trailingLabel = stringResource(R.string.checkBox_description),
                mushrooms = uiState.favorites,
                selectedMushroom = selectedMushroom,
                onSelect = { viewModel.onMushroomSelected(it) },
                onNavigate = { viewModel.onMushroomViewing(it); navigateToMushroom() }
            )
            //others column
            MushroomSelectionSection(
                title = stringResource(R.string.mushroom_list_others),
                isFavorites = false,
                trailingLabel = stringResource(R.string.checkBox_description),
                mushrooms = uiState.otherMushrooms,
                selectedMushroom = selectedMushroom,
                onSelect = { viewModel.onMushroomSelected(it) },
                onNavigate = { viewModel.onMushroomViewing(it); navigateToMushroom() }
            )
            Spacer(modifier = Modifier.height(padding.inner))
        }

    }
}

/** A single row in the mushroom list.
 *Tapping the row navigates to the detail screen.
 */
@Composable
private fun MushroomListItem(
    mushroom: Mushroom,
    isSelected: Boolean,
    onClick: () -> Unit,// called when the checkbox is toggled
    navigateToMushroom: () -> Unit,// called when the row itself is tapped
) {

    Surface(
        shadowElevation = padding.shadowElevationSmall,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .mushroomCard(isSelected)
                .clickable {
                    navigateToMushroom()
                }
                .padding(padding.outer),
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
            Spacer(Modifier.width(padding.inner))
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
            // Checkbox selects this mushroom for the map heatmap and historical finds
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

/**
 * Extension on [Modifier] that applies the mushroom card border style.
 * Selected cards get a thicker, colored border to indicate they are active on the map.
 */
@Composable
private fun Modifier.mushroomCard(isSelected: Boolean): Modifier {
    val borderWidth = if (isSelected) 4.dp else 2.dp
    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.primary
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

/**
 * Promotional banner for Soppkontroll.no — a third-party service where users
 * can verify whether a mushroom is safe to eat.
 */
@Composable
private fun MushroomControlBanner(onVisitSoppkontroll: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 4.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.large
            ),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        shadowElevation = padding.shadowElevationSmall
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding.outer),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Logo + text row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.soppkontroll_logo),
                    contentDescription = stringResource(R.string.soppkontroll_brand),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Soppkontroll.no",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.soppkontroll_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Orange button that opens soppkontroll in app
            Button(
                onClick = onVisitSoppkontroll,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07B20)),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.soppkontroll_visit_button), color = Color.White
                )
                Spacer(Modifier.width(padding.inner))
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

/**
 * A labeled section containing a list of mushroom rows.
 *
 * Displays a header with [title] on the left and an optional [trailingLabel] on the right
 * (used to hint that the checkbox selects a mushroom for the map).
 * Each mushroom in [mushrooms] is rendered as a [MushroomListItem].
 */
@Composable
private fun MushroomSelectionSection(
    title: String,
    isFavorites: Boolean,
    trailingLabel: String? = null,
    mushrooms: List<Mushroom>,
    selectedMushroom: Mushroom?,
    onSelect: (Mushroom) -> Unit,
    onNavigate: (Mushroom) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = padding.inner),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        if (trailingLabel != null) {
            Text(text = trailingLabel, style = MaterialTheme.typography.labelLarge)
        }
    }
    if (isFavorites && mushrooms.isEmpty()) {
        Text(
            text = "Her kan du legge til favorittsoppene dine",
            style = MaterialTheme.typography.labelLarge
        )
    }
    mushrooms.forEach { mushroom ->
        MushroomListItem(
            mushroom = mushroom,
            isSelected = selectedMushroom?.commonName == mushroom.commonName,
            onClick = { onSelect(mushroom) },
            navigateToMushroom = { onNavigate(mushroom) }
        )
    }
}

