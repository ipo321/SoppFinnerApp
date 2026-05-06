package no.uio.ifi.in2000.team16.ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LineAxis
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.ui.map.MapViewModel
import no.uio.ifi.in2000.team16.ui.navigation.INFO_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.MENU_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.POISON_CONTROL
import no.uio.ifi.in2000.team16.ui.navigation.SCORE_INFO_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.SECURITY_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.SETTINGS_SCREEN
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme

data class MenuItemData(
    val text: String,
    val icon: ImageVector,
    val route: String)

@Composable
fun MenuScreen(
    mapViewModel: MapViewModel,
    navController: NavController,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
) {
    val menuItems = listOf(
        MenuItemData("Mørkemodus", Icons.Default.Lightbulb, MENU_SCREEN),
        MenuItemData("Velg beregningsalgoritme", Icons.Default.LineAxis, MENU_SCREEN),
        MenuItemData("Hvordan beregnes scoren?", Icons.AutoMirrored.Filled.HelpOutline, SCORE_INFO_SCREEN),
        MenuItemData("Personvernerklæring", Icons.Default.Lock, SECURITY_SCREEN),
        MenuItemData("Ring Giftinformasjonen",Icons.Default.Call,POISON_CONTROL),
        MenuItemData("Om appen", Icons.Default.Info, INFO_SCREEN)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(
                start = Team16Theme.padding.outerMedium,
                end = Team16Theme.padding.outerMedium,
                top = Team16Theme.padding.outerMedium
            ),
    ) {
        Text(
            text = stringResource(R.string.meny),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        Spacer(modifier = Modifier.height(Team16Theme.padding.outerLarge))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Team16Theme.padding.innerMedium)
        ) {
            menuItems.forEach { item ->
                MenuListItem(
                    mapViewModel = mapViewModel,
                    item = item,
                    onClick = {
                        if (item.text == "Mørkemodus") { // For dark mode we want toggle and no route
                            onToggleTheme()
                        } else { // For the rest we want to navigate
                            navController.navigate(item.route)
                        }
                    },
                    trailingContent = if (item.text == "Mørkemodus") {
                        { // For dark mode we want the toggle to be visible
                            Toggle(
                                checked = darkTheme,
                                onCheckedChange = onToggleTheme
                            )
                        }
                    } else null // for the rest we don't want a trailing icon
                )
            }
            // Adds the image of the cute mushroom to the menu screen
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(R.drawable.cute_mushroom),
                contentDescription = "Søt sopp",
                modifier = Modifier
                    .padding(Team16Theme.padding.outerLarge)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun MenuListItem(
    mapViewModel: MapViewModel,
    algorithmViewModel: AlgorithmViewModel = hiltViewModel(),
    item: MenuItemData,
    onClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val mapState by mapViewModel.uiState.collectAsState()
    val algorithmState by algorithmViewModel.uiState.collectAsState()
    val context = mapState.currentContext

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(Team16Theme.padding.outer))

            Text(
                text = item.text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (trailingContent != null) {
                trailingContent()
            }
        }
        if (item.text == "Velg beregningsalgoritme") {
            Spacer(modifier = Modifier.height(Team16Theme.padding.outerMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Team16Theme.padding.inner, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                algorithmState.availableAlgorithms.forEach { algo ->
                    OutlinedButton(
                        onClick = onClick,
                        modifier = Modifier.wrapContentSize(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                        content = { Text(
                            text = algo,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        ) }
                    )
                }
            }
        }
    }
}
