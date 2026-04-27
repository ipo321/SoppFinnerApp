package no.uio.ifi.in2000.team16.ui.minSide

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
    viewModel: ProfileScreenViewModel = hiltViewModel()
){
    val context = LocalContext.current

    //Test to so if its loading in the terminal:
    println("InfoScreen loaded")


    val poisonInformationNmbr = "22591300"
    val intentPIN = Intent(Intent.ACTION_DIAL).apply{
        data = Uri.parse("tel:$poisonInformationNmbr")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD4E6CC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Column(modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Trykk for å ringe Gift Informasjonen")

                Button(
                    onClick = {
                        context.startActivity(intentPIN)
                    }
                ) {
                    Text("Ring")
                }
            }
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

