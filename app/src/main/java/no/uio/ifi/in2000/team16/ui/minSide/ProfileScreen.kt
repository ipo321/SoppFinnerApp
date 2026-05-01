package no.uio.ifi.in2000.team16.ui.minSide

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team16.ui.navigation.INFO_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.POISON_CONTROLL
import no.uio.ifi.in2000.team16.ui.navigation.SCORE_INFO_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.SECURITY_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.SETTINGS_SCREEN
import no.uio.ifi.in2000.team16.ui.navigation.SHROOMCONTROL_SCREEN

data class MenuItemData(
    val text: String,
    val icon: ImageVector,
    val route: String)

@Composable
fun ProfileScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val menuItems = listOf(
        MenuItemData("Innstillinger", Icons.Default.Settings, SETTINGS_SCREEN),
        MenuItemData("Hvordan beregnes scoren?", Icons.Default.HelpOutline, SCORE_INFO_SCREEN),
        MenuItemData("Personvernerklæring", Icons.Default.Lock, SECURITY_SCREEN),
        MenuItemData("Ring giftsentralen",Icons.Default.Call,POISON_CONTROLL),
        MenuItemData("Om appen", Icons.Default.Info, INFO_SCREEN)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
    ) {
//        OutlinedButton(
//            onClick = onNavigateBack,
//            modifier = Modifier.fillMaxWidth(),
//            shape = MaterialTheme.shapes.extraLarge
//        ) {
//            Text("Tilbake")
//        }
        Text(
            text = "Meny",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            menuItems.forEach { item ->
                MenuListItem(
                    item = item,
                    onClick = {
                        navController.navigate(item.route)
                    }
                )
            }
        }
    }

}

@Composable
fun MenuListItem(
    item: MenuItemData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = item.text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
