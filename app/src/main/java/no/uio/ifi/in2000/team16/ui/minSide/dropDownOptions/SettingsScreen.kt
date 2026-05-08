package no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions

import android.R.attr.onClick
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.ui.common.MushroomActionButton
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreenViewModel
import no.uio.ifi.in2000.team16.ui.minSide.Toggle
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme


@Composable
fun SettingsScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    viewModel: ProfileScreenViewModel = hiltViewModel()

){
    //Test to so if its loading in the terminal:
    println("SettingsScreen loaded")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        OutlinedButton(
//        onClick = onNavigateBack,
//        modifier = Modifier.fillMaxWidth(),
//        shape = MaterialTheme.shapes.extraLarge,
//            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
//                contentColor = MaterialTheme.colorScheme.onPrimary
//            )
//    ) {
//        Text("Tilbake")
//    }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            MushroomActionButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "BackButton",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(Team16Theme.padding.inner))
                Text(
                    text = stringResource(R.string.back_button),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        Spacer(modifier=Modifier.height(24.dp))

        Text(
            text = "Innstillinger",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(modifier=Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mørk modus",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Toggle(
                checked = darkTheme,
                onCheckedChange = onToggleTheme
            )
        }


    }
}

