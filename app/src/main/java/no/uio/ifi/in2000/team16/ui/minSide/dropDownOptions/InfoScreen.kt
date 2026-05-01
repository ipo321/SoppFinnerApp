package no.uio.ifi.in2000.team16.ui.minSide

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController


@Composable
fun InfoScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: ProfileScreenViewModel = androidx.hilt.navigation.compose.hiltViewModel()

){
    //Test to so if its loading in the terminal:
    println("InfoScreen loaded")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {    OutlinedButton(
        onClick = onNavigateBack,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text("Tilbake")
    }
        Text(
            text = "Om appen",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(modifier=Modifier.height(24.dp))


    }
}

