package no.uio.ifi.in2000.team16.ui.mushroomInfo

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.MushroomProperty
import no.uio.ifi.in2000.team16.ui.common.BackButton
import no.uio.ifi.in2000.team16.ui.common.MushroomActionButton
import no.uio.ifi.in2000.team16.ui.common.MushroomImage
import no.uio.ifi.in2000.team16.ui.common.WarningBanner
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme.padding

/*
 * MushroomInfoScreen.kt
 *
 * Detail screen for a single mushroom: image, name, property badges, favorite
 * toggle, "use in map" action and a description paragraph. All content is
 * sourced from local resources — this screen makes no API calls.
 *
 * The mushroom shown comes from MushroomInfoViewModel.uiState.viewMushroom,
 * which is set by the list screen via onMushroomViewing(...) before navigation.
 */

/**
 * Detail screen for the mushroom referenced by MushroomInfoViewModel.uiState.viewMushroom.
 *

 * @param onNavigateBack    invoked when the user taps the back button
 * @param onMushroomControl invoked when the user taps the Soppkontroll.no link
 *                          in the warning banner
 * @param onNavigateToMap   invoked when the user taps "Velg i kart"; the
 *                          ViewModel is also told to make this mushroom the
 *                          app-wide selection so the map reacts to it
 */
@Composable
fun MushroomInfoScreen(
    onNavigateBack: () -> Unit,
    onMushroomControl: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: MushroomInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selected = uiState.viewMushroom ?: return
    val inSeason = uiState.isInSeason
    // Favorites are matched by commonName since that's the unique key
    // used by SpeciesRepository.
    val isFavorite = uiState.favorites.any { it.commonName == selected.commonName }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = padding.outer, padding.outer, padding.outer)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(
            alignment = Alignment.CenterStart,
            textColor = MaterialTheme.colorScheme.onSurface,
            onNavigateBack = onNavigateBack,
        )
        Spacer(modifier = Modifier.height(padding.outer))

        // Always-visible safety reminder that links out to soppkontroll.no.
        WarningBanner(
            text = stringResource(R.string.mushroom_info_warning_text),
            linkText = stringResource(R.string.mushroom_info_warning_link),
            onLinkClick = onMushroomControl
        )

        Spacer(modifier = Modifier.height(padding.outer))
        MushroomImage(selected, 200.dp, MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(padding.outerLarge))

        Text(
            text = selected.commonName,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = selected.latinName,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(padding.outer))

        // Property badges: an "in season / not in season" badge built from
        // current-month state, followed by the mushroom's static properties
        // (edible, common, etc.) defined in the catalog.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(padding.outer, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // The in-season badge is synthesized here because the value is
            // computed at runtime (depends on today's month) rather than
            // being part of the static catalog.
            MushroomPropertyBadge(
                MushroomProperty(
                    label = stringResource(
                        if (inSeason) R.string.mushroom_info_in_season
                        else R.string.mushroom_info_not_in_season
                    ),
                    iconRes = if (inSeason) R.drawable.check_icon else R.drawable.x_icon,
                    isNegative = !inSeason
                )
            )
            selected.properties.forEach { property ->
                MushroomPropertyBadge(property)
            }
        }
        Spacer(modifier = Modifier.height(padding.outerLarge))

        // Favorite toggle button.
        MushroomActionButton(
            onClick = { viewModel.toggleFavorite(selected) },
            contentColor = MaterialTheme.colorScheme.error
        ) {
            val favoriteLabel = stringResource(
                if (isFavorite) R.string.mushroom_info_remove_favorite
                else R.string.mushroom_info_add_favorite
            )
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = favoriteLabel
            )
            Spacer(Modifier.width(padding.inner))
            Text(
                text = favoriteLabel,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(padding.inner))

        //button that sends you to the map with that mushroom selected
        MushroomActionButton(onClick = {
            viewModel.onMushroomSelected(selected)
            onNavigateToMap()
        }) {
            Text(
                text = stringResource(R.string.mushroom_info_select_on_map),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.width(padding.inner))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(padding.outer))

        Text(
            text = stringResource(R.string.mushroom_info_about_header),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(padding.inner))
        Text(
            text = stringResource(id = selected.bioRes.toInt()),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(padding.outer))
    }
}

/**
 * Round badge showing one mushroom property (icon + label).
 *
 * If [MushroomProperty.isNegative] is true the badge uses the error color
 * scheme — used for things like "not in season" or warning properties — so
 * the user gets a consistent at-a-glance signal across the row.
 */
@Composable
fun MushroomPropertyBadge(property: MushroomProperty) {
    // Border and background pair: error colors for "negative" properties,
    // secondary container for everything else.
    val borderColor =
        if (property.isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
    val bgColor =
        if (property.isNegative) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer

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
        Spacer(modifier = Modifier.height(padding.inner))
        Text(
            text = property.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}