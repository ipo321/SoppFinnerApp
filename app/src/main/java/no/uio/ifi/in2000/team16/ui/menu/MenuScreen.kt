package no.uio.ifi.in2000.team16.ui.menu

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LineAxis
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.ui.navigation.ALGORITHM_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.INFO_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.MENU_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.POISON_CONTROL
import no.uio.ifi.in2000.team16.ui.navigation.SCORE_INFO_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.SECURITY_SCREEN
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme.padding

data class MenuItemData(
    val text: String,
    val icon: ImageVector,
    val iconDescription: String,
    val route: String)

@Composable
fun MenuScreen(
    navController: NavController,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
) {
    val menuItems = listOf(
        MenuItemData("Mørkemodus", Icons.Default.Lightbulb, "Lyspære", MENU_SCREEN),
        MenuItemData("Velg beregningsalgoritme", Icons.Default.LineAxis, "Stigende graf", ALGORITHM_SCREEN),
        MenuItemData("Hvordan beregnes scoren?", Icons.AutoMirrored.Filled.HelpOutline, "Spørsmålstegn", SCORE_INFO_SCREEN),
        MenuItemData("Personvernerklæring", Icons.Default.Lock, "Lås", SECURITY_SCREEN),
        MenuItemData("Ring Giftinformasjonen",Icons.Default.Call, "Telefon", POISON_CONTROL),
        MenuItemData("Om appen", Icons.Default.Info, "Informasjonssymbol", INFO_SCREEN)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(
                start = padding.outerMedium,
                end = padding.outerMedium,
                top = padding.outerMedium
            ),
    ) {
        Text(
            text = stringResource(R.string.meny),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        Spacer(modifier = Modifier.height(padding.outerLarge))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(padding.innerMedium)
        ) {
            menuItems.forEach { item ->
                MenuListItem(
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
                    .padding(padding.outerLarge)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun MenuListItem(
    item: MenuItemData,
    onClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Surface(
        shadowElevation = padding.shadowElevationSmall,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
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
                    contentDescription = item.iconDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(padding.outer))

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
        }
    }
}
