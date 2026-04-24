package no.uio.ifi.in2000.team16.ui.minSide

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoViewModel


@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: ProfileScreenViewModel = hiltViewModel()

){
    //Test to so if its loading in the terminal:
    println("HistoryScreen loaded")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD4E6CC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box{
            Text("This is the historyScreen")
        }

        Box {
            Button(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text("Go Back")
            }
        }
    }
}

