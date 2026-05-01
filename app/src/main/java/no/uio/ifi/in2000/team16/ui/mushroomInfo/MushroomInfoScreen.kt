package no.uio.ifi.in2000.team16.ui.mushroomInfo

import no.uio.ifi.in2000.team16.ui.common.MushroomActionButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.domain.model.MushroomProperty
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.TextButton
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import no.uio.ifi.in2000.team16.ui.common.BackButton
import no.uio.ifi.in2000.team16.ui.common.MushroomImage
import no.uio.ifi.in2000.team16.ui.common.WarningBanner


/* screen for mushroom info, it has a dropdown meny button that is clickable to change mushroom,
this screen only uses resorses that are in the app and no api calls are made.
see the MushroomInfoViewModel for the dropdown meny and the Mushroom data class.

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
    val isFavorite = uiState.favorites.any { it.commonName == selected.commonName }
    //main Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding.outer)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(Alignment.CenterStart, onNavigateBack)

        Spacer(modifier = Modifier.height(padding.outer))
        //warning with link to soppkontroll.no
        WarningBanner(
            text = "Aldri spis sopp du er usikker på.",
            linkText = "Se Soppkontroll.no for informasjon",
            onLinkClick = onMushroomControl
        )

        Spacer(modifier = Modifier.height(padding.outer))

        // Mushroom image
        MushroomImage(selected, 200.dp, MaterialTheme.colorScheme.onBackground)

        Spacer(modifier = Modifier.height(padding.outerLarge))

        //name text
        Text(
            text = selected.commonName,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = selected.latinName,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(padding.outer))

        //info badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(padding.outer, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //In Season badge
            MushroomPropertyBadge(
                MushroomProperty(
                    label = if (inSeason) "I sesong" else "Ikke i sesong",
                    iconRes = if (inSeason) R.drawable.check_icon else R.drawable.x_icon,
                    isNegative = !inSeason
                )
            )
            //rest of the badges
            selected.properties.forEach { property ->
                MushroomPropertyBadge(property)
            }
        }
        Spacer(modifier = Modifier.height(padding.outerLarge))

        //favorite button
        MushroomActionButton(
            onClick = { viewModel.toggleFavorite(selected) },
            contentColor = MaterialTheme.colorScheme.error
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Fjern fra favoritter" else "Legg til i favoritter"
            )
            Spacer(Modifier.width(padding.inner))
            Text(
                if (isFavorite) "Fjern fra favoritter" else "Legg til i favoritter",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(padding.inner))
        //GO to map Button
        MushroomActionButton(onClick = {
            viewModel.onMushroomSelected(selected)
            onNavigateToMap()
        }) {
            Text("Velg i kart")
            Spacer(Modifier.width(padding.inner))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(padding.outer))

        //about the mushroom header
        Text(
            text = "Om soppen",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(padding.inner))
        //the about the mushroom text
        Text(
            text = stringResource(id = selected.bioRes.toInt()),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun MushroomPropertyBadge(property: MushroomProperty) {
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
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun MushroomWarningBanner(onMushroomControl: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(padding.outer)
                .clickable { onMushroomControl() },
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(padding.inner))
            Column {
                Text(
                    text = "Aldri spis sopp du er usikker på.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Se Soppkontroll.no for informasjon",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textDecoration = TextDecoration.Underline
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BackToExploreButton(onClick: () -> Unit) {
    MushroomActionButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(Modifier.width(padding.inner))
        Text(
            text = "Utforsk",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun MushroomImage(selected: Mushroom, size : Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onBackground),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = selected.imageRes),
            contentDescription = selected.commonName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun MushroomActionButton(
    onClick: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.wrapContentSize(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
        content = content
    )
}